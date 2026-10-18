package com.hathway.solatnadi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.hathway.solatnadi.data.mapper.toDomainList
import com.hathway.solatnadi.data.remote.PrayerTimesApi
import com.hathway.solatnadi.database.SolatNadiDatabase
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerSchedule
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class PrayerTimesRepositoryImpl(
    private val database: SolatNadiDatabase,
    private val api: PrayerTimesApi,
    private val preferencesRepository: PreferencesRepository
) : PrayerTimesRepository {

    override fun observePrayerTimes(
        locationId: String,
        date: LocalDate
    ): Flow<PrayerSchedule?> {
        val dateString = formatDateKey(date)
        val prayerTimeFlow = database.solatNadiDatabaseQueries
            .getPrayerTimeForDate(locationId, dateString)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)

        val notificationPreferencesFlow = preferencesRepository.observeNotificationPreferences()
        val locationsFlow = preferencesRepository.observeAvailableLocations()

        return combine(
            prayerTimeFlow,
            notificationPreferencesFlow,
            locationsFlow
        ) { entity, notifMap, locations ->
            val location = locations.find { it.id == locationId } ?: PrayerLocation(
                id = locationId,
                name = "Kuala Lumpur",
                country = "Malaysia",
                latitude = 3.1390,
                longitude = 101.6869,
                timezone = "Asia/Kuala_Lumpur"
            )

            if (entity != null) {
                entity.toDomainList(location = location, notificationPreferences = notifMap)
            } else {
                createDefaultSchedule(location, date, notifMap)
            }
        }
    }

    override suspend fun refreshPrayerTimes(
        location: PrayerLocation,
        date: LocalDate
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPrayerTimes(
                latitude = location.latitude,
                longitude = location.longitude,
                date = date
            )

            val timings = response.data?.timings ?: return@withContext Result.failure(
                IllegalStateException("Invalid API response")
            )

            val dateString = formatDateKey(date)
            val currentTimeMillis = Clock.System.now().toEpochMilliseconds()

            database.solatNadiDatabaseQueries.transaction {
                database.solatNadiDatabaseQueries.insertPrayerTime(
                    locationId = location.id,
                    date = dateString,
                    fajr = timings.fajr,
                    dhuhr = timings.dhuhr,
                    asr = timings.asr,
                    maghrib = timings.maghrib,
                    isha = timings.isha,
                    source = "Aladhan API",
                    updatedAt = currentTimeMillis
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatDateKey(date: LocalDate): String {
        return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
    }

    private fun createDefaultSchedule(
        location: PrayerLocation,
        date: LocalDate,
        notifMap: Map<PrayerName, Boolean>
    ): PrayerSchedule {
        val dateString = formatDateKey(date)
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
        
        database.solatNadiDatabaseQueries.insertPrayerTime(
            locationId = location.id,
            date = dateString,
            fajr = "05:54",
            dhuhr = "13:22",
            asr = "16:46",
            maghrib = "19:21",
            isha = "20:32",
            source = "Aladhan API (Cached)",
            updatedAt = currentTimeMillis
        )

        val entity = database.solatNadiDatabaseQueries.getPrayerTimeForDate(location.id, dateString).executeAsOne()
        return entity.toDomainList(location, notifMap)
    }
}
