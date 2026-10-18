package com.hathway.solatnadi.domain.repository

import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface PrayerTimesRepository {
    fun observePrayerTimes(
        locationId: String,
        date: LocalDate
    ): Flow<PrayerSchedule?>

    suspend fun refreshPrayerTimes(
        location: PrayerLocation,
        date: LocalDate
    ): Result<Unit>
}
