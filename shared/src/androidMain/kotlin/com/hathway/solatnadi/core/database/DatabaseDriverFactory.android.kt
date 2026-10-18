package com.hathway.solatnadi.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hathway.solatnadi.database.SolatNadiDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SolatNadiDatabase.Schema,
            context = context,
            name = "solatnadi.db"
        )
    }
}
