package com.hathway.solatnadi.domain.model

enum class TimeFormat {
    FORMAT_12H, FORMAT_24H
}

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class Madhab(val displayName: String, val subtitle: String, val schoolCode: Int) {
    SHAFI("Shafi'i", "(Standard)", 0),
    HANAFI("Hanafi", "(Alternative)", 1)
}

data class AppPreferences(
    val calculationMethod: Int = 3,
    val madhab: Madhab = Madhab.SHAFI,
    val timeFormat: TimeFormat = TimeFormat.FORMAT_12H,
    val appTheme: AppTheme = AppTheme.LIGHT,
    val isAutoLocationEnabled: Boolean = false
)
