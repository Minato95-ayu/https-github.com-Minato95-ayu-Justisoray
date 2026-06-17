package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CaseRepository(private val caseDao: CaseDao) {
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()

    fun getTimelineForCase(caseId: Int): Flow<List<TimelineItemEntity>> {
        return caseDao.getTimelineByCaseId(caseId)
    }

    fun getEvidenceForCase(caseId: Int): Flow<List<EvidenceEntity>> {
        return caseDao.getEvidenceByCaseId(caseId)
    }

    suspend fun getCase(id: Int): CaseEntity? {
        return caseDao.getCaseById(id)
    }

    suspend fun insertCase(case: CaseEntity): Long {
        return caseDao.insertCase(case)
    }

    suspend fun insertTimelineItem(item: TimelineItemEntity) {
        caseDao.insertTimelineItem(item)
    }

    suspend fun insertEvidenceItem(item: EvidenceEntity) {
        caseDao.insertEvidenceItem(item)
    }

    suspend fun updateCase(case: CaseEntity) {
        caseDao.updateCase(case)
    }

    suspend fun updateTimelineItem(item: TimelineItemEntity) {
        caseDao.updateTimelineItem(item)
    }

    suspend fun updateEvidenceItem(item: EvidenceEntity) {
        caseDao.updateEvidenceItem(item)
    }

    suspend fun deleteCase(caseId: Int) {
        caseDao.deleteCase(caseId)
        caseDao.deleteTimelinesByCaseId(caseId)
        caseDao.deleteEvidenceByCaseId(caseId)
    }

    suspend fun createPrepopulatedFIRCase(): Int {
        val caseId = caseDao.insertCase(
            CaseEntity(
                title = "Refusal of FIR at Police Station",
                summary = "Critical citizen complaint ignored by Station Officer on grounds of jurisdictional excuse or civil classification.",
                environment = "POLICE_STATION",
                status = "Investigation-Active",
                confidence = 35
            )
        ).toInt()

        // Create standard pre-configured timeline items
        val timelineItems = listOf(
            TimelineItemEntity(
                caseId = caseId,
                type = "EVENT",
                title = "Initial Encounter & Incident",
                description = "Physical assault and verbal threat in public space by high-influence local construction contractors.",
                relativeTime = "Past - Day 01",
                statusText = "COMPLETED"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "EVENT",
                title = "Attempted Police Report",
                description = "Entered Sector 4 police desk with a written complaint. Duty Officer refuses stating 'This is a family mutual land dispute, settle it yourselves'.",
                relativeTime = "Past - Day 02",
                statusText = "COMPLETED"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "RIGHT",
                title = "Unlock Right: Register FIR (Sec. 154 CrPC)",
                description = "If a cognizable offense has occurred, the officer is contractually obligated under Indian Law to register an FIR immediately (Lalita Kumari mandate).",
                relativeTime = "Immediate Rights",
                statusText = "UNLOCKED",
                lawSource = "Section 154 Code of Criminal Procedure"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "EVIDENCE",
                title = "Evidence Check: Primary Written Complaint",
                description = "Prepare two signed copies of the complaint detailing exact dates, times, and witness references.",
                relativeTime = "Day 03",
                statusText = "PENDING"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "EVIDENCE",
                title = "Evidence Check: Electronic Audio/Video Record",
                description = "Video recording showing officer refusing the receipt of the physical document.",
                relativeTime = "Day 03",
                statusText = "PENDING"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "PROCEDURE",
                title = "Escalation: Superintendent of Police",
                description = "Submit written complaint via Registered Post with Acknowlegdment Direct Card to the Superintendent (SP) under Section 154(3).",
                relativeTime = "Future - Days 04 to 10",
                statusText = "PENDING",
                lawSource = "Section 154(3) CrPC"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "ACTION",
                title = "Magistrate Court Direct Motion",
                description = "File a private criminal complaints petition under Sec. 156(3) with the local judicial magistrate to order a police investigation.",
                relativeTime = "Future - Day 15+",
                statusText = "PENDING",
                lawSource = "Section 156(3) Criminal Procedure"
            )
        )

        timelineItems.forEach { caseDao.insertTimelineItem(it) }

        // Create standard pre-configured evidence nodes
        val evidenceNodes = listOf(
            EvidenceEntity(
                caseId = caseId,
                name = "Signed Physical Complaint Copy",
                description = "Double physical copy detailing Contractors assault with date, time stamps.",
                category = "Primary",
                classification = "Documentary",
                admissibility = "Requires registered acknowledgement post receipt",
                statusText = "FOUND",
                assignedNodeX = 200f,
                assignedNodeY = 150f
            ),
            EvidenceEntity(
                caseId = caseId,
                name = "Video Recording of Non-Compliance",
                description = "Smartphone recording capture of the Duty Officer refusing to file the FIR copy.",
                category = "Secondary",
                classification = "Digital",
                admissibility = "Requires Electronic Signature Certificate (Sec. 65B IE Act)",
                statusText = "MISSING",
                assignedNodeX = 500f,
                assignedNodeY = 180f
            ),
            EvidenceEntity(
                caseId = caseId,
                name = "Medical Injury Memo Report",
                description = "Outpatient physical slip confirming contusions and skin fractures from local clinic register.",
                category = "Primary",
                classification = "Documentary",
                admissibility = "Requires doctor signature verification under Sec. 45 IE Act",
                statusText = "FOUND",
                assignedNodeX = 350f,
                assignedNodeY = 320f
            ),
            EvidenceEntity(
                caseId = caseId,
                name = "Eyewitness Statement: Sunil Kumar",
                description = "Local chai vendor Sunil Kumar confirms contractors blockaded path and initiated group assault.",
                category = "Corroborative",
                classification = "Witness Statement",
                admissibility = "Statement under Sec. 161 CrPC during active trial examination",
                statusText = "SUSPECTED",
                assignedNodeX = 650f,
                assignedNodeY = 350f
            )
        )

        evidenceNodes.forEach { caseDao.insertEvidenceItem(it) }

        return caseId
    }

    suspend fun createPrepopulatedLabourCase(): Int {
        val caseId = caseDao.insertCase(
            CaseEntity(
                title = "Employer Salary Unlawful Theft",
                summary = "Private corporate entity withholding three consecutive months salary with zero communication, violating payment laws.",
                environment = "LABOUR_OFFICE",
                status = "Investigation-Active",
                confidence = 25
            )
        ).toInt()

        val timelineItems = listOf(
            TimelineItemEntity(
                caseId = caseId,
                type = "EVENT",
                title = "Termination & Non-Payment",
                description = "Withheld 3 complete performance salary cycles during standard operational project delivery.",
                relativeTime = "Past - Day 01",
                statusText = "COMPLETED"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "RIGHT",
                title = "Unlock Right: Compensation Protection",
                description = "Statutes enforce that employees must be paid within 7 to 10 days of the closing payroll cycle.",
                relativeTime = "Statutory Rights",
                statusText = "UNLOCKED",
                lawSource = "Section 5, Payment of Wages Act"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "EVIDENCE",
                title = "Evidence: Salary Employment Contract",
                description = "Locate official Employment Offer contract containing signed remuneration, roles, and notice clauses.",
                relativeTime = "Day 02",
                statusText = "COMPLETED"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "EVIDENCE",
                title = "Evidence: Performance Logs & Email Threads",
                description = "Official project logs showing active work delivery during the disputed salary period.",
                relativeTime = "Day 03",
                statusText = "PENDING"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "PROCEDURE",
                title = "Legal Notice Formulation",
                description = "Issue a formal, contract-oriented Written Demand Notice for Unpaid Wages to corporate board members.",
                relativeTime = "Future - Days 05 to 12",
                statusText = "PENDING"
            ),
            TimelineItemEntity(
                caseId = caseId,
                type = "ACTION",
                title = "Filing with Labour Commisioner",
                description = "Submit formal digital application detail to the regional Labour Office Commissioner office for wage recovery arbitration.",
                relativeTime = "Future - Day 20+",
                statusText = "PENDING",
                lawSource = "Section 15, Payment of Wages Act"
            )
        )

        timelineItems.forEach { caseDao.insertTimelineItem(it) }

        val evidenceNodes = listOf(
            EvidenceEntity(
                caseId = caseId,
                name = "Employment Agreement/LOU",
                description = "Signed mutual employment and performance contract.",
                category = "Primary",
                classification = "Documentary",
                admissibility = "Admissible upon standard execution proof (Sec. 67 IE Act)",
                statusText = "FOUND",
                assignedNodeX = 250f,
                assignedNodeY = 160f
            ),
            EvidenceEntity(
                caseId = caseId,
                name = "3 Months Bank Ledger Bank Statements",
                description = "Official bank statements proving no deposit execution during payroll months.",
                category = "Primary",
                classification = "Documentary",
                admissibility = "Admissible under Bankers' Books Evidence Act 1891",
                statusText = "FOUND",
                assignedNodeX = 420f,
                assignedNodeY = 280f
            ),
            EvidenceEntity(
                caseId = caseId,
                name = "Corporate Slack/Email Work Logs",
                description = "Work archives verifying work task completions and code checkins.",
                category = "Secondary",
                classification = "Digital",
                admissibility = "Requires Section 65B Electronics Certificate",
                statusText = "MISSING",
                assignedNodeX = 640f,
                assignedNodeY = 180f
            )
        )

        evidenceNodes.forEach { caseDao.insertEvidenceItem(it) }
        return caseId
    }
}
