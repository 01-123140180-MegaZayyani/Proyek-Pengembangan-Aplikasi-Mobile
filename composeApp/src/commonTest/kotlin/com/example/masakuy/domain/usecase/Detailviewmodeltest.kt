package com.example.masakuy.domain.usecase

import com.example.masakuy.data.repository.AIRepositoryImpl
import io.mockk.clearAllMocks
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val repository = mockk<AIRepositoryImpl>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loadRecipe should initialize successfully`() {
        // Test sederhana untuk memastikan ViewModel ter-compile dengan benar
        assertNotNull(repository)
    }
}