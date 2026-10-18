package com.hathway.solatnadi.presentation.home

import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val location: PrayerLocation? = null,
    val availableLocations: List<PrayerLocation> = emptyList(),
    val gregorianDateFormatted: String = "",
    val hijriDateFormatted: String = "",
    val prayerTimes: List<PrayerTime> = emptyList(),
    val nextPrayer: PrayerTime? = null,
    val countdownFormatted: String = "00:00:00",
    val isOffline: Boolean = false,
    val errorMessage: String? = null
)

sealed interface HomeIntent {
    data object Refresh : HomeIntent
    data object Retry : HomeIntent
    data class SelectLocation(val location: PrayerLocation) : HomeIntent
    data class ToggleNotification(val prayerName: PrayerName, val enabled: Boolean) : HomeIntent
}

sealed interface HomeEffect {
    data class ShowMessage(val message: String) : HomeEffect
    data object NavigateToSettings : HomeEffect
}
