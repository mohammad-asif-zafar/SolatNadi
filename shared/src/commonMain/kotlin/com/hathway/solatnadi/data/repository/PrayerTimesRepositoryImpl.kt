package com.hathway.solatnadi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.hathway.solatnadi.data.mapper.estimateHijriDate
import com.hathway.solatnadi.data.mapper.formatGregorianDate
import com.hathway.solatnadi.data.mapper.toDomainList
import com.hathway.solatnadi.data.remote.PrayerTimesApi
import com.hathway.solatnadi.database.SolatNadiDatabase
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerSchedule
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
        val appPreferencesFlow = preferencesRepository.observeAppPreferences()

        return combine(
            prayerTimeFlow,
            notificationPreferencesFlow,
            locationsFlow,
            appPreferencesFlow
        ) { entity, notifMap, locations, appPrefs ->
            val location = locations.find { it.id == locationId } ?: PrayerLocation(
                id = locationId,
                name = "Kuala Lumpur",
                country = "Malaysia",
                latitude = 3.1390,
                longitude = 101.6869,
                timezone = "Asia/Kuala_Lumpur"
            )

            entity?.toDomainList(
                location = location,
                notificationPreferences = notifMap,
                timeFormat = appPrefs.timeFormat
            )
        }
    }

    override suspend fun refreshPrayerTimes(
        location: PrayerLocation,
        date: LocalDate
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val appPrefs = preferencesRepository.observeAppPreferences().first()
            val response = api.getPrayerTimes(
                latitude = location.latitude,
                longitude = location.longitude,
                date = date,
                method = appPrefs.calculationMethod,
                school = appPrefs.madhab.schoolCode
            )

            val timings = response.data?.timings ?: return@withContext Result.failure(
                IllegalStateException("Invalid API response")
            )

            val greg = response.data.date.gregorian
            val hijri = response.data.date.hijri

            val gregorianDateStr = if (greg != null) {
                val weekday = greg.weekday?.en?.take(3) ?: ""
                val day = greg.date.split("-").firstOrNull() ?: date.dayOfMonth.toString()
                val month = greg.month?.en?.take(3) ?: ""
                val year = greg.year
                "$weekday, $day $month $year"
            } else {
                formatGregorianDate(date)
            }

            val hijriDateStr = if (hijri != null) {
                val day = hijri.day
                val month = hijri.month?.en ?: "Rabiulakhir"
                val year = hijri.year
                val desig = hijri.designation?.abbreviated ?: "AH"
                "$day $month $year $desig"
            } else {
                estimateHijriDate(date)
            }

            val dateString = formatDateKey(date)
            @OptIn(ExperimentalTime::class)
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
                    gregorianDate = gregorianDateStr,
                    hijriDate = hijriDateStr,
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
}
