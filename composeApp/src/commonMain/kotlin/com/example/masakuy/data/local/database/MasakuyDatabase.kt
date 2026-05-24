package com.example.masakuy.data.local.database

import app.cash.sqldelight.db.SqlDriver
import com.example.masakuy.data.local.MasakuyDatabase as GeneratedDatabase

class MasakuyDatabase(sqlDriver: SqlDriver) {
    private val database = GeneratedDatabase(sqlDriver)
    val recipeQueries = database.recipeQueries
}