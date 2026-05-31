package com.example.masakuy.domain.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    fun getRecommendation(
        budget: Int,
        ingredients: List<String> = emptyList(),
        preferences: String = ""
    ): Flow<Result<List<Recipe>>>

    fun getRecipeDetail(recipeName: String, budget: Int): Flow<Result<RecipeDetail>>
}