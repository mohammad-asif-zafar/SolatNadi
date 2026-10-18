package com.hathway.solatnadi.domain.model

import kotlinx.datetime.LocalDateTime

data class PrayerTime(
    val prayerName: PrayerName,
    val timeString: String,
    val scheduledDateTime: LocalDateTime,
    val isNotificationEnabled: Boolean = true,
    val isNext: Boolean = false
)
