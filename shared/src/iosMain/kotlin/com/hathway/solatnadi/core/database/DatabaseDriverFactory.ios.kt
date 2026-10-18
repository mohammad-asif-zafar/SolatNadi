package com.hathway.solatnadi.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.hathway.solatnadi.database.SolatNadiDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SolatNadiDatabase.Schema,
            name = "solatnadi.db"
        )
    }
}
