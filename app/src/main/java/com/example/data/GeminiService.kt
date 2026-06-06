package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import android.util.Log

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

class GeminiApiRepository {
    private val tag = "GeminiApiRepository"
    private val baseUrl = "https://generativelanguage.googleapis.com/"

    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    // 60-second timeouts specifically tailored for generative AI tasks (MANDATE from skill guidelines)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Calls Gemini live, or falls back to an offline high-fidelity klinical reasoning engine
     * if the key is missing or network isn't available.
     */
    suspend fun consultCompanion(
        apiKey: String,
        situation: String,
        diagnosisId: String,
        phaseId: String
    ): String {
        // Validation check for empty or placeholder key
        val isKeyPlaceholder = apiKey.isEmpty() || 
                              apiKey == "MY_GEMINI_API_KEY" || 
                              apiKey.startsWith("MY_")
        
        if (isKeyPlaceholder) {
            Log.w(tag, "No valid Gemini API key configured. Executing rich clinical offline fallback matrix.")
            return OfflineMatrixEngine.matchAdvice(phaseId, diagnosisId, situation)
        }

        val fullPrompt = """
            SITUATIVE DIAGNOSTIK / ANFRAGE:
            - Akute Situation: $situation
            - Diagnose-Kontext: $diagnosisId
            - Aktuelle deeskalierende Phase: $phaseId
            
            Gib mir einen passgenauen, konkreten deeskalativen Ratschlag nach den KJP-Richtlinien.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = fullPrompt)))),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.4f, // low temperature to promote clinical precision 
                maxOutputTokens = 800
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = GeminiSystemPrompt.SYSTEM_PROMPT)))
        )

        return try {
            Log.d(tag, "Consulting Live Gemini 3.5 API...")
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Fehler: Antwort von Gemini war leer."
        } catch (e: Exception) {
            Log.e(tag, "Gemini API call failed: ${e.message}. Launching offline clinical deescalation engine.")
            OfflineMatrixEngine.matchAdvice(phaseId, diagnosisId, situation)
        }
    }
}
