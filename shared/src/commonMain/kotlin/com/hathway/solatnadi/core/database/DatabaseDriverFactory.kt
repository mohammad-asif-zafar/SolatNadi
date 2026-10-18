package com.hathway.solatnadi.core.database

import app.cash.sqldelight.db.SqlDriver
import com.hathway.solatnadi.database.SolatNadiDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): SolatNadiDatabase {
    val driver = driverFactory.createDriver()
    return SolatNadiDatabase(driver)
}
