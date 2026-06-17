package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini Request / Response Models ---

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiConfig(
    @Json(name = "temperature") val temperature: Float? = 0.2f,
    @Json(name = "responseMimeType") val responseMimeType: String? = "application/json"
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

// --- Structuring Output Parsers ---

@JsonClass(generateAdapter = true)
data class AIResolvedCase(
    val title: String,
    val summary: String,
    val environment: String, // "POLICE_STATION", "COURTROOM", "CYBER_CRIME", "CONSUMER_FORUM", "LABOUR_OFFICE", "FAMILY_COURT"
    val confidence: Int,     // 0-100 indicating case detail strength
    val timeline: List<AITimelineItem>,
    val evidence: List<AIEvidenceItem>
)

@JsonClass(generateAdapter = true)
data class AITimelineItem(
    val type: String,        // "EVENT", "RIGHT", "EVIDENCE", "PROCEDURE", "ACTION"
    val title: String,
    val description: String,
    val relativeTime: String,
    val lawSource: String? = null
)

@JsonClass(generateAdapter = true)
data class AIEvidenceItem(
    val name: String,
    val description: String,
    val category: String,       // "Primary", "Secondary", "Corroborative"
    val classification: String, // "Documentary", "Digital", "Witness Statement", "Physical"
    val admissibility: String
)

// --- Retrofit API Service ---

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun analyzeCase(userPrompt: String): AIResolvedCase? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null // Fallback to simulated offline scenarios if key is unconfigured
        }

        val systemPrompt = """
            You are a supreme legal architecture and investigative reasoning system. 
            The user specifies a legal problem, a real dispute, or a civil/criminal complaint.
            Your role is to translate this legal conflict into a visual event blueprint that models it as an experience.
            
            Return the result in strictly valid JSON matching the following schema structure:
            {
               "title": "Title of the legal encounter screen",
               "summary": "Cohesive, supportive summary of current crisis",
               "environment": "Must select one of: POLICE_STATION, COURTROOM, CYBER_CRIME, CONSUMER_FORUM, LABOUR_OFFICE, FAMILY_COURT, PROPERTY_REGISTRY",
               "confidence": 42, // integer between 10 and 95 reflecting completeness
               "timeline": [
                  {
                     "type": "EVENT", // Must select one of: EVENT, RIGHT, EVIDENCE, PROCEDURE, ACTION
                     "title": "Event Name",
                     "description": "Specific tactical action detail",
                     "relativeTime": "Timeline phase (e.g. Past - Day 01, Immediate Right, Future - Days 05-10)",
                     "lawSource": "Cite actual relevant section code (e.g. Section 154 CrPC, Section 420 IPC, Section 43 IT Act) with strict factuality. Avoid hallucinating statutory sources."
                  }
               ],
               "evidence": [
                  {
                     "name": "Evidence file record name",
                     "description": "Short diagnostic description of what this node represents",
                     "category": "Primary", // Must select: Primary, Secondary, Corroborative
                     "classification": "Documentary", // Must select: Documentary, Digital, Witness Statement, Physical
                     "admissibility": "Direct summary of legal weight and requirements (e.g. Requires Section 65B IT Act Electronic certificate, Admissible under Shop Establishments Act)"
                  }
               ]
            }
            
            Guidelines:
            - Focus purely on legal facts, rights, evidence tracking, and action plans.
            - Provide real legal citations appropriate to the jurisdiction.
            - Ensure timelines and evidence structures are complete and directly linked.
            - Do not include markdown code fence wrappers or unescaped text outside of the raw JSON payload.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = "User dispute report: $userPrompt")))
            ),
            generationConfig = GeminiConfig(
                temperature = 0.2f,
                responseMimeType = "application/json"
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        return try {
            val response = api.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                val adapter = moshi.adapter(AIResolvedCase::class.java)
                adapter.fromJson(jsonText)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
