package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.presentation.screens.api.GeminiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for AIRepositoryImpl success path and Result mapping.
 *
 * GeminiService is a final class wrapping HttpClient + BuildConfig + Android
 * Log, so it's mocked with MockK rather than instantiated directly.
 *
 * Requires test dependency:
 *   testImplementation("io.mockk:mockk:1.13.x")
 */
class AIRepositoryImplTest {

    private val geminiService = mockk<GeminiService>()
    private val repository = AIRepositoryImpl(geminiService)

    private fun sampleRecipes(budget: Int) = listOf(
        Recipe(id = "gemini_1", name = "Nasi Goreng", image = "", estimatedCost = 10000, estimatedTime = 30, difficulty = "Mudah", isFavorite = false),
        Recipe(id = "gemini_2", name = "Tempe Goreng", image = "", estimatedCost = 5000, estimatedTime = 15, difficulty = "Mudah", isFavorite = false),
        Recipe(id = "gemini_3", name = "Sayur Asem", image = "", estimatedCost = 8000, estimatedTime = 25, difficulty = "Mudah", isFavorite = false),
        Recipe(id = "gemini_4", name = "Telur Dadar", image = "", estimatedCost = 6000, estimatedTime = 10, difficulty = "Mudah", isFavorite = false),
        Recipe(id = "gemini_5", name = "Tahu Bacem", image = "", estimatedCost = 7000, estimatedTime = 20, difficulty = "Mudah", isFavorite = false)
    ).map { it.copy(estimatedCost = it.estimatedCost.coerceAtMost(budget)) }

    private fun sampleDetail(name: String, budget: Int) = RecipeDetail(
        id = "gemini_detail_${name.hashCode()}",
        name = name,
        image = "",
        estimatedCost = budget,
        estimatedTime = 30,
        difficulty = "Mudah",
        ingredients = listOf(
            Ingredient(name = "beras", quantity = "", estimatedPrice = 3000),
            Ingredient(name = "telur", quantity = "", estimatedPrice = 4000)
        ),
        instructions = listOf("Langkah pertama memasak", "Langkah kedua memasak")
    )

    // ---- getRecommendation: success ----

    @Test
    fun `getRecommendation emits Loading then Success with 5 recipes`() = runTest {
        val budget = 15000
        coEvery { geminiService.getRecommendation(budget) } returns sampleRecipes(budget)

        val emissions = repository.getRecommendation(budget).toList()

        assertEquals(Result.Loading, emissions[0])
        val success = emissions[1] as Result.Success
        assertEquals(5, success.data.size)
    }

    @Test
    fun `getRecommendation success result respects budget constraint`() = runTest {
        val budget = 8000
        coEvery { geminiService.getRecommendation(budget) } returns sampleRecipes(budget)

        val result = repository.getRecommendation(budget).toList().last() as Result.Success

        assertTrue(result.data.all { it.estimatedCost <= budget })
    }

    @Test
    fun `getRecommendation passes ingredients and preferences without error even though GeminiService ignores them`() = runTest {
        val budget = 20000
        coEvery { geminiService.getRecommendation(budget) } returns sampleRecipes(budget)

        val result = repository.getRecommendation(
            budget = budget,
            ingredients = listOf("ayam", "tempe"),
            preferences = "pedas"
        ).toList().last() as Result.Success

        assertEquals(5, result.data.size)
    }

    @Test
    fun `getRecommendation with empty list from GeminiService returns Success with empty list`() = runTest {
        val budget = 1000
        coEvery { geminiService.getRecommendation(budget) } returns emptyList()

        val result = repository.getRecommendation(budget).toList().last() as Result.Success

        assertTrue(result.data.isEmpty())
    }

    // ---- getRecipeDetail: success ----

    @Test
    fun `getRecipeDetail emits Loading then Success with parsed detail`() = runTest {
        val name = "Nasi Goreng"
        val budget = 15000
        coEvery { geminiService.getRecipeDetail(name, budget) } returns sampleDetail(name, budget)

        val emissions = repository.getRecipeDetail(name, budget).toList()

        assertEquals(Result.Loading, emissions[0])
        val success = emissions[1] as Result.Success
        assertEquals(name, success.data.name)
        assertEquals(2, success.data.ingredients.size)
        assertEquals(2, success.data.instructions.size)
    }

    @Test
    fun `getRecipeDetail success result has correct estimatedCost from budget`() = runTest {
        val name = "Soto Ayam"
        val budget = 20000
        coEvery { geminiService.getRecipeDetail(name, budget) } returns sampleDetail(name, budget)

        val result = repository.getRecipeDetail(name, budget).toList().last() as Result.Success

        assertEquals(budget, result.data.estimatedCost)
    }

    @Test
    fun `getRecipeDetail success result has isFavorite false by default`() = runTest {
        val name = "Rendang"
        val budget = 30000
        coEvery { geminiService.getRecipeDetail(name, budget) } returns sampleDetail(name, budget)

        val result = repository.getRecipeDetail(name, budget).toList().last() as Result.Success

        assertEquals(false, result.data.isFavorite)
    }
}