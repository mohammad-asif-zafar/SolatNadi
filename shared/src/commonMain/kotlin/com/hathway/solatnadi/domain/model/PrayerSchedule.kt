package com.hathway.solatnadi.domain.model

import kotlinx.datetime.LocalDate

data class PrayerSchedule(
    val location: PrayerLocation,
    val date: LocalDate,
    val gregorianDateFormatted: String,
    val hijriDateFormatted: String,
    val prayerTimes: List<PrayerTime>,
    val nextPrayer: PrayerTime?,
    val source: String = "Aladhan API"
)
