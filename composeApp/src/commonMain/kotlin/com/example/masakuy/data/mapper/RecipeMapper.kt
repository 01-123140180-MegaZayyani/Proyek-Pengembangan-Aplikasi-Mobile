package com.example.masakuy.data.mapper

import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RecipeMapper {
    fun mapRecipeDetailToRecipe(detail: RecipeDetail): Recipe {
        return Recipe(
            id = detail.id,
            name = detail.name,
            image = detail.image,
            estimatedCost = detail.estimatedCost,
            estimatedTime = detail.estimatedTime,
            difficulty = detail.difficulty,
            isFavorite = detail.isFavorite
        )
    }

    fun parseIngredients(json: String): List<Ingredient> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseInstructions(json: String): List<String> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeIngredients(ingredients: List<Ingredient>): String {
        return try {
            Json.encodeToString(ingredients)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun serializeInstructions(instructions: List<String>): String {
        return try {
            Json.encodeToString(instructions)
        } catch (e: Exception) {
            "[]"
        }
    }
}
