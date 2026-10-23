package com.hathway.solatnadi.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.hathway.solatnadi.database.SolatNadiDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = NativeSqliteDriver(
            schema = SolatNadiDatabase.Schema,
            name = "solatnadi.db"
        )
        try {
            driver.execute(null, "PRAGMA journal_mode = WAL;", 0)
            driver.execute(null, "PRAGMA busy_timeout = 5000;", 0)
        } catch (_: Exception) {}
        return driver
    }
}
