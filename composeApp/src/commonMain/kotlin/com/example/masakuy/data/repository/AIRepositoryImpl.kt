package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.presentation.screens.api.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override fun getRecommendation(
        budget: Int,
        ingredients: List<String>,
        preferences: String
    ): Flow<Result<List<Recipe>>> = flow {
        emit(Result.Loading)
        try {
            val recipes = geminiService.getRecommendation(budget)
            emit(Result.Success(recipes))
        } catch (e: GeminiService.RateLimitException) {
            emit(Result.Error(e))
        } catch (e: GeminiService.ApiException) {
            emit(Result.Error(e))
        } catch (e: Exception) {
            emit(Result.Error(Exception("Gagal terhubung ke AI.")))
        }
    }

    override fun getRecipeDetail(recipeName: String, budget: Int): Flow<Result<RecipeDetail>> = flow {
        emit(Result.Loading)
        try {
            val detail = geminiService.getRecipeDetail(recipeName, budget)
            emit(Result.Success(detail))
        } catch (e: GeminiService.ApiException) {
            emit(Result.Error(e))
        } catch (e: Exception) {
            emit(Result.Error(Exception("Gagal ambil detail resep.")))
        }
    }
}