package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecommendationUseCase
import com.example.masakuy.presentation.screens.recommendation.RecommendationViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRecommendationUseCase: GetRecommendationUseCase
    private lateinit var viewModel: RecommendationViewModel

    private val dummyRecipes = listOf(
        Recipe(id = "r1", name = "Nasi Goreng",  image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r2", name = "Mie Goreng",   image = "", estimatedCost = 12000, estimatedTime = 15, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r3", name = "Soto Ayam",    image = "", estimatedCost = 20000, estimatedTime = 30, difficulty = "Sedang", isFavorite = false),
        Recipe(id = "r4", name = "Tempe Goreng", image = "", estimatedCost = 8000,  estimatedTime = 10, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r5", name = "Sayur Sop",    image = "", estimatedCost = 18000, estimatedTime = 25, difficulty = "Mudah",  isFavorite = false),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecommendationUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRecommendations sukses mengisi recipes di uiState`() = runTest {
        coEvery {
            getRecommendationUseCase(budget = 50000, ingredients = emptyList())
        } returns flowOf(Result.Success(dummyRecipes))

        viewModel = RecommendationViewModel(getRecommendationUseCase, testDispatcher)
        viewModel.getRecommendations(50000)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(5, state.recipes.size)
    }

    @Test
    fun `getRecommendations Loading mengubah isLoading menjadi true`() = runTest {
        coEvery {
            getRecommendationUseCase(budget = 30000, ingredients = emptyList())
        } returns flowOf(Result.Loading)

        viewModel = RecommendationViewModel(getRecommendationUseCase, testDispatcher)
        viewModel.getRecommendations(30000)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `getRecommendations Error biasa set pesan error dan isLoading false`() = runTest {
        val exception = Exception("Gagal terhubung ke AI.")
        coEvery {
            getRecommendationUseCase(budget = 30000, ingredients = emptyList())
        } returns flowOf(Result.Error(exception))

        viewModel = RecommendationViewModel(getRecommendationUseCase, testDispatcher)
        viewModel.getRecommendations(30000)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null, "Pesan error di UI State tidak boleh null")
    }

    @Test
    fun `toggleIngredient menambah ingredient jika belum ada`() = runTest {
        viewModel = RecommendationViewModel(mockk(), testDispatcher)
        viewModel.toggleIngredient("Telur")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.selectedIngredients.contains("Telur"))
    }
}