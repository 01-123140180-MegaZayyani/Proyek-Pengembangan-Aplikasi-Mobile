package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepositoryImpl(
    private val database: MasakuyDatabase,
    private val mapper: RecipeMapper
) : RecipeRepository {

    override fun getAllRecipes(): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getAllRecipes().executeAsList()
            emit(Result.Success(recipes.map {
                Recipe(it.id, it.name, it.image, it.estimatedCost.toInt(), it.estimatedTime.toInt(), it.difficulty, it.isFavorite == 1L)
            }))
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    override fun getRecipeById(id: String): Flow<Result<RecipeDetail>> = flow {
        try {
            emit(Result.Loading)
            val recipe = database.recipeQueries.getRecipeById(id).executeAsOneOrNull()
            if (recipe != null) {
                emit(Result.Success(RecipeDetail(
                    id = recipe.id, name = recipe.name, image = recipe.image,
                    estimatedCost = recipe.estimatedCost.toInt(), estimatedTime = recipe.estimatedTime.toInt(),
                    difficulty = recipe.difficulty, ingredients = mapper.parseIngredients(recipe.ingredients),
                    instructions = mapper.parseInstructions(recipe.instructions), isFavorite = recipe.isFavorite == 1L
                )))
            } else {
                emit(Result.Error(Exception("Recipe tidak ditemukan")))
            }
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    override fun searchRecipes(query: String): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.searchRecipes(query).executeAsList()
            emit(Result.Success(recipes.map {
                Recipe(it.id, it.name, it.image, it.estimatedCost.toInt(), it.estimatedTime.toInt(), it.difficulty, it.isFavorite == 1L)
            }))
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    override fun getRecipesByBudget(budget: Int): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getRecipesByBudget(budget.toLong()).executeAsList()
            emit(Result.Success(recipes.map {
                Recipe(it.id, it.name, it.image, it.estimatedCost.toInt(), it.estimatedTime.toInt(), it.difficulty, it.isFavorite == 1L)
            }))
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    override suspend fun saveFavorite(recipeId: String, isFavorite: Boolean) {
        database.recipeQueries.updateFavorite(if (isFavorite) 1L else 0L, recipeId)
    }

    override fun getFavoriteRecipes(): Flow<Result<List<Recipe>>> = flow {
        try {
            emit(Result.Loading)
            val recipes = database.recipeQueries.getFavorites().executeAsList()
            emit(Result.Success(recipes.map {
                Recipe(it.id, it.name, it.image, it.estimatedCost.toInt(), it.estimatedTime.toInt(), it.difficulty, it.isFavorite == 1L)
            }))
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    override suspend fun insertRecipe(detail: RecipeDetail) {
        database.recipeQueries.insertRecipe(
            id = detail.id, name = detail.name, image = detail.image,
            estimatedCost = detail.estimatedCost.toLong(), estimatedTime = detail.estimatedTime.toLong(),
            difficulty = detail.difficulty, ingredients = mapper.serializeIngredients(detail.ingredients),
            instructions = mapper.serializeInstructions(detail.instructions),
            isFavorite = if (detail.isFavorite) 1L else 0L
        )
    }
}
