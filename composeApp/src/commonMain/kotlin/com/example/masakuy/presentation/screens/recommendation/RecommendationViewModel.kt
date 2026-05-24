package com.example.masakuy.presentation.screens.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecommendationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecommendationUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedIngredients: List<String> = emptyList(),
    val retryCountdown: Int = 0  // countdown detik, 0 = tidak ada countdown
)

class RecommendationViewModel(
    private val getRecommendationUseCase: GetRecommendationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    private var lastBudget: Int = 0
    private var countdownJob: Job? = null

    fun getRecommendations(budget: Int) {
        lastBudget = budget
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(retryCountdown = 0)

        viewModelScope.launch {
            getRecommendationUseCase(
                budget = budget,
                ingredients = _uiState.value.selectedIngredients
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null,
                            retryCountdown = 0
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            recipes = result.data,
                            isLoading = false,
                            error = null,
                            retryCountdown = 0
                        )
                    }
                    is Result.Error -> {
                        val errorMsg = result.exception.message ?: "Error tidak diketahui"
                        val isRateLimit = errorMsg.contains("429") ||
                                errorMsg.contains("quota") ||
                                errorMsg.contains("rate") ||
                                errorMsg.contains("banyak")

                        // Kalau rate limit, parse detik dari pesan atau default 60 detik
                        val waitSeconds = if (isRateLimit) {
                            Regex("\\d+").find(errorMsg)?.value?.toIntOrNull() ?: 60
                        } else 0

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = if (isRateLimit)
                                "Terlalu banyak permintaan. Coba lagi dalam $waitSeconds detik."
                            else errorMsg,
                            retryCountdown = waitSeconds
                        )

                        // Start auto-retry countdown kalau rate limit
                        if (isRateLimit && waitSeconds > 0) {
                            startCountdown(waitSeconds, budget)
                        }
                    }
                }
            }
        }
    }

    private fun startCountdown(seconds: Int, budget: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _uiState.value = _uiState.value.copy(
                    retryCountdown = i,
                    error = "Terlalu banyak permintaan. Coba lagi dalam $i detik."
                )
                delay(1000)
            }
            // Auto-retry setelah countdown selesai
            _uiState.value = _uiState.value.copy(retryCountdown = 0, error = null)
            getRecommendations(budget)
        }
    }

    fun toggleIngredient(ingredient: String) {
        val current = _uiState.value.selectedIngredients.toMutableList()
        if (current.contains(ingredient)) current.remove(ingredient)
        else current.add(ingredient)
        _uiState.value = _uiState.value.copy(selectedIngredients = current)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}