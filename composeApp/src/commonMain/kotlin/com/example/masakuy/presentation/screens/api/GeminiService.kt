package com.example.masakuy.presentation.screens.api

import android.util.Log
import com.example.masakuy.BuildConfig
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GeminiService(private val httpClient: HttpClient) {

    private val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
    private val MODEL = "llama-3.3-70b-versatile"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    class RateLimitException(val retryAfterSeconds: Long) :
        Exception("Terlalu banyak permintaan. Coba lagi dalam ${retryAfterSeconds} detik.")
    class ApiException(message: String) : Exception(message)

    @Serializable
    data class GroqRequest(val model: String, val messages: List<Message>, val max_tokens: Int = 1000)
    @Serializable
    data class Message(val role: String, val content: String)
    @Serializable
    data class GroqResponse(val choices: List<Choice>? = null)
    @Serializable
    data class Choice(val message: Message? = null)

    private suspend fun callGroq(prompt: String, maxTokens: Int = 1000): String {
        val rawBody = try {
            val response = httpClient.post(BASE_URL) {
                header("Authorization", "Bearer ${BuildConfig.GEMINI_API_KEY}")
                contentType(ContentType.Application.Json)
                setBody(GroqRequest(model = MODEL, messages = listOf(Message("user", prompt)), max_tokens = maxTokens))
            }
            Log.d("GEMINI", "▶ Status = ${response.status}")
            response.body<String>()
        } catch (e: ClientRequestException) {
            Log.e("GEMINI", "✖ Error: ${e.response.status.value} - ${e.message}")
            when (e.response.status.value) {
                429 -> throw RateLimitException(30L)
                401 -> throw ApiException("API key tidak valid.")
                else -> throw ApiException("Error ${e.response.status.value}")
            }
        } catch (e: Exception) {
            Log.e("GEMINI", "✖ Exception: ${e.message}")
            throw ApiException("Koneksi gagal: ${e.message}")
        }
        Log.d("GEMINI", "▶ Raw = $rawBody")
        return json.decodeFromString<GroqResponse>(rawBody)
            .choices?.firstOrNull()?.message?.content
            ?: throw ApiException("Response kosong")
    }

    // ── Rekomendasi ──────────────────────────────────────────────

    suspend fun getRecommendation(budget: Int): List<Recipe> {
        Log.d("GEMINI", "▶ getRecommendation budget=$budget")
        val text = callGroq(buildRecommendationPrompt(budget), maxTokens = 500)
        Log.d("GEMINI", "▶ Text = $text")
        val recipes = parseToRecipes(text, budget)
        Log.d("GEMINI", "▶ Jumlah resep = ${recipes.size}")
        return recipes
    }

    private fun buildRecommendationPrompt(budget: Int): String = """
        Berikan tepat 5 rekomendasi resep masakan Indonesia yang mudah dibuat
        dengan budget maksimal Rp$budget per porsi. Semua harga HARUS di bawah Rp$budget.
        
        Balas HANYA dengan format ini, tanpa teks lain sama sekali:
        1. Nama Resep - Rp10000
        2. Nama Resep - Rp12000
        3. Nama Resep - Rp8000
        4. Nama Resep - Rp15000
        5. Nama Resep - Rp9000
    """.trimIndent()

    private fun parseToRecipes(text: String, budget: Int): List<Recipe> {
        return text.split("\n").filter { it.isNotBlank() }.mapNotNull { line ->
            val name = listOf(
                Regex("""^\d+[.)]\s*\*{0,2}(.+?)\*{0,2}\s*[-–]\s*Rp[\d.,]+"""),
                Regex("""^\d+[.)]\s*\*{0,2}(.+?)\*{0,2}\s*$"""),
                Regex("""^\d+[.)]\s*(.+)""")
            ).firstNotNullOfOrNull { regex ->
                regex.find(line.trim())?.groupValues?.getOrNull(1)
                    ?.replace("*", "")?.trim()?.takeIf { it.isNotBlank() }
            } ?: return@mapNotNull null

            val cost = Regex("""Rp([\d.,]+)""").find(line)?.groupValues?.getOrNull(1)
                ?.replace(".", "")?.replace(",", "")?.toIntOrNull() ?: budget

            Recipe(
                id = "gemini_${System.currentTimeMillis()}_${name.hashCode()}",
                name = name,
                image = "",
                estimatedCost = cost.coerceAtMost(budget),
                estimatedTime = 30,
                difficulty = "Mudah",
                isFavorite = false
            )
        }
    }

    // ── Detail ───────────────────────────────────────────────────

    suspend fun getRecipeDetail(recipeName: String, budget: Int): RecipeDetail {
        Log.d("GEMINI", "▶ getRecipeDetail: $recipeName")
        val text = callGroq(buildDetailPrompt(recipeName, budget), maxTokens = 1000)
        Log.d("GEMINI", "▶ Detail text = $text")
        return parseToRecipeDetail(text, recipeName, budget)
    }

    private fun buildDetailPrompt(name: String, budget: Int): String = """
        Berikan detail resep masakan Indonesia: "$name" dengan budget Rp$budget.
        
        Balas HANYA dengan format ini, tanpa teks lain:
        WAKTU: 30
        KESULITAN: Mudah
        BAHAN:
        - 200g beras | Rp3000
        - 2 butir telur | Rp4000
        - 3 siung bawang putih | Rp1000
        CARA:
        1. Langkah pertama memasak
        2. Langkah kedua memasak
        3. Langkah ketiga memasak
    """.trimIndent()

    private fun parseToRecipeDetail(text: String, name: String, budget: Int): RecipeDetail {
        val lines = text.lines()
        var waktu = 30
        var kesulitan = "Mudah"
        val bahan = mutableListOf<Ingredient>()
        val cara = mutableListOf<String>()
        var section = ""

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("WAKTU:") ->
                    waktu = trimmed.removePrefix("WAKTU:").trim().toIntOrNull() ?: 30
                trimmed.startsWith("KESULITAN:") ->
                    kesulitan = trimmed.removePrefix("KESULITAN:").trim()
                trimmed == "BAHAN:" -> section = "bahan"
                trimmed == "CARA:" -> section = "cara"
                trimmed.startsWith("-") && section == "bahan" -> {
                    val parts = trimmed.removePrefix("-").trim().split("|")
                    val bahanName = parts.getOrNull(0)?.trim() ?: continue
                    val harga = Regex("""Rp([\d.,]+)""").find(parts.getOrNull(1) ?: "")
                        ?.groupValues?.get(1)?.replace(".", "")?.replace(",", "")
                        ?.toIntOrNull() ?: 0
                    bahan.add(Ingredient(name = bahanName, quantity = "", estimatedPrice = harga))
                }
                trimmed.matches(Regex("""^\d+\..+""")) && section == "cara" ->
                    cara.add(trimmed.replaceFirst(Regex("""^\d+\.\s*"""), ""))
            }
        }

        return RecipeDetail(
            id = "gemini_detail_${name.hashCode()}",
            name = name,
            image = "",
            estimatedCost = budget,
            estimatedTime = waktu,
            difficulty = kesulitan,
            ingredients = bahan,
            instructions = cara,
            isFavorite = false
        )
    }
}