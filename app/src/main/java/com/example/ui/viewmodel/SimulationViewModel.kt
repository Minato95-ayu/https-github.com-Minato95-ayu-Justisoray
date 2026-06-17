package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SimulationViewModel(application: Application) : AndroidViewModel(application) {
    private val database = CaseDatabase.getDatabase(application)
    private val repository = CaseRepository(database.caseDao())

    // --- State Streams ---
    val casesState: StateFlow<List<CaseEntity>> = repository.allCases
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCaseId = MutableStateFlow<Int?>(null)
    val selectedCaseId: StateFlow<Int?> = _selectedCaseId.asStateFlow()

    val selectedCase: StateFlow<CaseEntity?> = _selectedCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf<CaseEntity?>(null)
            else flow { emit(repository.getCase(id)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTimeline: StateFlow<List<TimelineItemEntity>> = _selectedCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getTimelineForCase(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeEvidence: StateFlow<List<EvidenceEntity>> = _selectedCaseId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getEvidenceForCase(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI/AI Operations State ---
    var isLoadingAI by mutableStateOf(false)
        private set

    var aiErrorText by mutableStateOf<String?>(null)
        private set

    var inputPrompt by mutableStateOf("")

    // Initialize default scenarios on first database launch
    init {
        viewModelScope.launch {
            casesState.first() // Wait for first collect
            if (casesState.value.isEmpty()) {
                // Populate default interactive scenarios
                val firId = repository.createPrepopulatedFIRCase()
                repository.createPrepopulatedLabourCase()
                _selectedCaseId.value = firId
            } else {
                _selectedCaseId.value = casesState.value.firstOrNull()?.id
            }
        }
    }

    fun selectCase(caseId: Int) {
        _selectedCaseId.value = caseId
        resetCinemaProgress()
    }

    fun deleteCurrentCase() {
        val currentId = _selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.deleteCase(currentId)
            val updated = repository.allCases.first()
            if (updated.isNotEmpty()) {
                _selectedCaseId.value = updated.first().id
            } else {
                _selectedCaseId.value = null
            }
        }
    }

    // --- Interactive Evidence Pinboard Operations ---
    fun updateEvidenceNodePosition(evidenceId: Int, newX: Float, newY: Float) {
        viewModelScope.launch {
            activeEvidence.value.find { it.id == evidenceId }?.let { item ->
                repository.updateEvidenceItem(item.copy(assignedNodeX = newX, assignedNodeY = newY))
            }
        }
    }

    fun toggleEvidenceCollected(evidenceId: Int) {
        viewModelScope.launch {
            activeEvidence.value.find { it.id == evidenceId }?.let { item ->
                val nextStatus = if (item.statusText == "FOUND") "MISSING" else "FOUND"
                repository.updateEvidenceItem(item.copy(statusText = nextStatus))
                
                // Recalculate case confidence score based on physical admissibility ratios
                recalculateCaseConfidence()
            }
        }
    }

    private suspend fun recalculateCaseConfidence() {
        val currentId = _selectedCaseId.value ?: return
        val currentCase = repository.getCase(currentId) ?: return
        val evidenceList = activeEvidence.value
        if (evidenceList.isEmpty()) return

        val foundCount = evidenceList.count { it.statusText == "FOUND" }
        val newConfidence = ((foundCount.toFloat() / evidenceList.size.toFloat()) * 85f + 10f).toInt()
        repository.updateCase(currentCase.copy(confidence = newConfidence))
    }

    // --- Dynamic AI Interactive Generation ---
    fun submitCustomDispute() {
        val prompt = inputPrompt.trim()
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            isLoadingAI = true
            aiErrorText = null
            try {
                val result = GeminiClient.analyzeCase(prompt)
                if (result != null) {
                    // Save case to local Room memory
                    val newCaseId = repository.insertCase(
                        CaseEntity(
                            title = result.title,
                            summary = result.summary,
                            environment = result.environment,
                            status = "Investigation-Active",
                            confidence = result.confidence
                        )
                    ).toInt()

                    // Insert resolved Timelines
                    result.timeline.forEach { item ->
                        repository.insertTimelineItem(
                            TimelineItemEntity(
                                caseId = newCaseId,
                                type = item.type,
                                title = item.title,
                                description = item.description,
                                relativeTime = item.relativeTime,
                                statusText = "UNLOCKED",
                                lawSource = item.lawSource
                            )
                        )
                    }

                    // Insert resolved Evidence pinboards
                    kotlin.random.Random
                    result.evidence.forEachIndexed { index, item ->
                        val nodeX = 150f + (index % 2) * 320f + (0..40).random().toFloat()
                        val nodeY = 120f + (index / 2) * 220f + (0..40).random().toFloat()
                        repository.insertEvidenceItem(
                            EvidenceEntity(
                                caseId = newCaseId,
                                name = item.name,
                                description = item.description,
                                category = item.category,
                                classification = item.classification,
                                admissibility = item.admissibility,
                                statusText = "MISSING",
                                assignedNodeX = nodeX,
                                assignedNodeY = nodeY
                            )
                        )
                    }

                    _selectedCaseId.value = newCaseId
                    inputPrompt = ""
                } else {
                    aiErrorText = "The AI Reasoning System is currently offline or the Gemini API Key is missing in AI Studio Secrets. Displaying fallback instructions below."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                aiErrorText = "Connection timed out. Run the pre-loaded high-fidelity scenarios locally instead!"
            } finally {
                isLoadingAI = false
            }
        }
    }

    // --- Cinematic Scenario Storyboard State ---
    var currentSlide by mutableStateOf(1)
        private set
    var sceneFrozen by mutableStateOf(false)
        private set
    var userDecision by mutableStateOf<Int?>(null)
        private set
    var consequencesRevealed by mutableStateOf(false)
        private set
    var formalActionPlanText by mutableStateOf("")
        private set

    fun resetCinemaProgress() {
        currentSlide = 1
        sceneFrozen = false
        userDecision = null
        consequencesRevealed = false
        formalActionPlanText = ""
    }

    fun nextSlide() {
        if (currentSlide < 8) {
            currentSlide++
            if (currentSlide == 3) {
                sceneFrozen = true // Timeline freezes visually!
            } else if (currentSlide != 3 && currentSlide != 5 && currentSlide != 6) {
                sceneFrozen = false
            }
        }
    }

    fun selectDecisionOption(optionId: Int) {
        userDecision = optionId
        consequencesRevealed = true
        
        // Generate a localized Action Draft/LOD based on choice
        formalActionPlanText = when (optionId) {
            1 -> """
                [WARNING: COUNTERPRODUCTIVE ACTION ESCALATION PATH]
                
                Aggressive engagement at desk level did not result in an FIR receipt, but has raised situational friction parameters.
                
                ACTION DENSITY DRAFT:
                "To the Commissioner of Police,
                REGARDING: Obstructive refusal of Duty Officer at Sector 4 Station in registering a Cognizable assault complaint on 17/06/2026.
                
                Please take judicial notice of active physical body scan reports and statutory non-compliance."
            """.trimIndent()
            
            2 -> """
                [STRATEGIC ACTION PATHWAY UNLOCKED]
                
                Your demand for a written refusal letter forces the Duty Officer to comply with standard administrative protocols or face departmental audits!
                
                ACTION DENSITY DRAFT:
                "To the Station House Officer, Sector 4,
                SUBJECT: Request for Written Statement under Sec 154(1) CrPC regarding complaint refusal.
                
                I hereby submit duplicate physical copy of assault complaints. If this complaint is classified as non-cognizable, please provide signed written refusal specifying civil/non-cognizable grounds under law regulations."
            """.trimIndent()
            
            3 -> """
                [OPTIMAL PENAL ESCALATION INITIATED - Sec. 154(3) & 156(3)]
                
                By bypassing local bias and routing through Judicial channels, the Magistrate is legally empowered to issue dynamic warrants directing investigations.
                
                ACTION DENSITY DRAFT:
                "IN THE COURT OF THE METROPOLITAN MAGISTRATE,
                PETITION NO: _____ OF 2026
                
                Under Section 156(3) of Criminal Procedure Code,
                In Re: Assault and Aggrieved Contractor Obstruction complaint.
                
                THE PETITIONER RESPECTFULLY SHOWETH:
                1. That local authorities refused filing on 17/06/2026.
                2. Medical and video records demonstrate cognizable criminality.
                3. Therefore, Petitioner prays that this Court directs the Station Head to file complete FIR records immediately."
            """.trimIndent()
            
            else -> ""
        }
    }
}
