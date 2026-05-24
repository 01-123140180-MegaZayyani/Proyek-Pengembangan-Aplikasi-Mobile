package com.example.masakuy.presentation.screens.api

import com.example.masakuy.BuildConfig
import com.example.masakuy.presentation.screens.api.GeminiDto.GeminiRequest
import com.example.masakuy.presentation.screens.api.GeminiDto.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class GeminiService(private val httpClient: HttpClient) {

    private val BASE_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Custom exception untuk rate limit
    class RateLimitException(retryAfterSeconds: Long) :
        Exception("Terlalu banyak permintaan. Coba lagi dalam ${retryAfterSeconds} detik.")

    class ApiException(message: String) : Exception(message)

    suspend fun getRecommendation(
        budget: Int,
        ingredients: List<String>
    ): List<String> {
        val prompt = buildPrompt(budget, ingredients)

        val httpResponse = httpClient.post(BASE_URL) {
            parameter("key", BuildConfig.GEMINI_API_KEY)
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        mapOf("parts" to listOf(mapOf("text" to prompt)))
                    )
                )
            )
        }

        val rawBody = httpResponse.body<String>()

        // Handle error status
        when (httpResponse.status) {
            HttpStatusCode.TooManyRequests -> {
                // Ekstrak waktu retry dari message
                val retrySeconds = Regex("""retry in ([\d.]+)s""")
                    .find(rawBody)?.groupValues?.getOrNull(1)
                    ?.toDoubleOrNull()?.toLong() ?: 30L
                throw RateLimitException(retrySeconds)
            }
            HttpStatusCode.Unauthorized -> {
                throw ApiException("API key tidak valid. Periksa konfigurasi.")
            }
            HttpStatusCode.NotFound -> {
                throw ApiException("Model tidak ditemukan. Hubungi developer.")
            }
        }

        if (!httpResponse.status.value.toString().startsWith("2")) {
            throw ApiException("Error dari server: ${httpResponse.status}")
        }

        val response = json.decodeFromString<GeminiResponse>(rawBody)
        val text = response.candidates
            ?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

        if (text.isBlank()) return emptyList()

        return text.split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                listOf(
                    Regex("""^\d+\.\s*\*{0,2}(.+?)\*{0,2}\s*[-–]\s*Rp[\d.,]+"""),
                    Regex("""^\d+[.)]\s*\*{0,2}(.+?)\*{0,2}\s*$"""),
                    Regex("""^\d+[.)]\s*(.+)""")
                ).firstNotNullOfOrNull { regex ->
                    regex.find(line.trim())?.groupValues?.getOrNull(1)
                        ?.replace("*", "")?.trim()
                        ?.takeIf { it.isNotBlank() }
                }
            }
    }

    private fun buildPrompt(budget: Int, ingredients: List<String>): String {
        val ingredientList = if (ingredients.isNotEmpty())
            "\nBahan yang tersedia: ${ingredients.joinToString(", ")}"
        else ""

        return """
            Berikan tepat 5 rekomendasi resep masakan Indonesia yang mudah dibuat 
            dengan budget Rp$budget per porsi.$ingredientList
            
            Balas HANYA dengan format ini, tanpa teks lain sama sekali:
            1. Nama Resep - Rp10000
            2. Nama Resep - Rp12000
            3. Nama Resep - Rp8000
            4. Nama Resep - Rp15000
            5. Nama Resep - Rp9000
        """.trimIndent()
    }
}