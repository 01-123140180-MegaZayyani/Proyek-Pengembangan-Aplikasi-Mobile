package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import com.example.masakuy.presentation.screens.search.SearchViewModel
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var viewModel: SearchViewModel

    // PERBAIKAN: Nilai estimatedTime diubah menjadi angka Int murni (20, 30, 90)
    private val recipeNasiGoreng = Recipe(id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah", isFavorite = false)
    private val recipeSotoAyam = Recipe(id = "r2", name = "Soto Ayam", image = "", estimatedCost = 20000, estimatedTime = 30, difficulty = "Sedang", isFavorite = false)
    private val recipeRendang = Recipe(id = "r3", name = "Rendang Sapi", image = "", estimatedCost = 50000, estimatedTime = 90, difficulty = "Sulit", isFavorite = false)
    private val allRecipes = listOf(recipeNasiGoreng, recipeSotoAyam, recipeRendang)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecipesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateQuery memperbarui field query di uiState`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(allRecipes))
        viewModel = SearchViewModel(getRecipesUseCase)

        viewModel.updateQuery("Nasi")
        advanceUntilIdle()

        assertEquals("Nasi", viewModel.uiState.value.query)
    }

    @Test
    fun `search mengembalikan resep yang namanya mengandung query`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(allRecipes))
        viewModel = SearchViewModel(getRecipesUseCase)

        viewModel.search("nasi")
        advanceUntilIdle()

        val results = viewModel.uiState.value.results
        assertEquals(1, results.size)
        assertEquals("Nasi Goreng", results[0].name)
    }
}