package com.example.masakuy.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.masakuy.data.local.MasakuyDatabase

class DatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = MasakuyDatabase.Schema,
            context = context,
            name = "masakuy.db"
        )
    }
}