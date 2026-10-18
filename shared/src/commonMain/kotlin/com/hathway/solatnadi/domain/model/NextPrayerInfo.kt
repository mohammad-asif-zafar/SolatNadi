package com.hathway.solatnadi.domain.model

data class NextPrayerInfo(
    val prayer: PrayerTime,
    val remainingSeconds: Long,
    val formattedCountdown: String,
    val progress: Float
)
