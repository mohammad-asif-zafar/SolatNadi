package com.hathway.solatnadi.domain.model

enum class TimeFormat {
    FORMAT_12H, FORMAT_24H
}

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class AppPreferences(
    val calculationMethod: Int = 3,
    val timeFormat: TimeFormat = TimeFormat.FORMAT_12H,
    val appTheme: AppTheme = AppTheme.LIGHT,
    val isAutoLocationEnabled: Boolean = false
)
