package com.hathway.solatnadi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.hathway.solatnadi.data.mapper.toDomain
import com.hathway.solatnadi.database.SolatNadiDatabase
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.AppTheme
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.TimeFormat
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PreferencesRepositoryImpl(
    private val database: SolatNadiDatabase,
    private val settings: Settings = Settings()
) : PreferencesRepository {

    private val defaultLocation = PrayerLocation(
        id = "kl_my",
        name = "Kuala Lumpur",
        country = "Malaysia",
        latitude = 3.1390,
        longitude = 101.6869,
        timezone = "Asia/Kuala_Lumpur",
        isSelected = true
    )

    private val presetLocations = listOf(
        defaultLocation,
        PrayerLocation("jb_my", "Johor Bahru", "Malaysia", 1.4927, 103.7414, "Asia/Kuala_Lumpur"),
        PrayerLocation("penang_my", "George Town", "Malaysia", 5.4164, 100.3327, "Asia/Kuala_Lumpur"),
        PrayerLocation("mecca_sa", "Mecca", "Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        PrayerLocation("medina_sa", "Medina", "Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh"),
        PrayerLocation("london_uk", "London", "United Kingdom", 51.5074, -0.1278, "Europe/London"),
        PrayerLocation("sg", "Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
        PrayerLocation("jakarta_id", "Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta")
    )

    init {
        val queries = database.solatNadiDatabaseQueries
        presetLocations.forEach { loc ->
            queries.insertLocation(
                id = loc.id,
                name = loc.name,
                country = loc.country,
                latitude = loc.latitude,
                longitude = loc.longitude,
                timezone = loc.timezone,
                isSelected = if (loc.id == defaultLocation.id) 1L else 0L
            )
        }
    }

    override fun observeSelectedLocation(): Flow<PrayerLocation> {
        return database.solatNadiDatabaseQueries
            .selectSelectedLocation()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.toDomain() ?: defaultLocation
            }
    }

    override suspend fun setSelectedLocation(location: PrayerLocation) = withContext(Dispatchers.IO) {
        val queries = database.solatNadiDatabaseQueries
        queries.clearSelectedLocation()
        queries.insertLocation(
            id = location.id,
            name = location.name,
            country = location.country,
            latitude = location.latitude,
            longitude = location.longitude,
            timezone = location.timezone,
            isSelected = 1L
        )
    }

    override fun observeAvailableLocations(): Flow<List<PrayerLocation>> {
        return database.solatNadiDatabaseQueries
            .selectAllLocations()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                if (list.isEmpty()) presetLocations else list.map { it.toDomain() }
            }
    }

    override fun observeNotificationPreferences(): Flow<Map<PrayerName, Boolean>> {
        return database.solatNadiDatabaseQueries
            .getNotificationPreferences()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                val map = list.associate { entity ->
                    val prayerName = PrayerName.entries.find { it.name == entity.prayerName }
                    (prayerName ?: PrayerName.FAJR) to (entity.enabled == 1L)
                }.toMutableMap()

                PrayerName.entries.forEach { prayer ->
                    if (!map.containsKey(prayer)) {
                        map[prayer] = true
                    }
                }
                map
            }
    }

    override suspend fun toggleNotificationPreference(prayerName: PrayerName, enabled: Boolean) = withContext(Dispatchers.IO) {
        database.solatNadiDatabaseQueries.insertOrUpdateNotificationPreference(
            prayerName = prayerName.name,
            enabled = if (enabled) 1L else 0L,
            soundPreference = "DEFAULT"
        )
    }

    override fun observeAppPreferences(): Flow<AppPreferences> {
        return database.solatNadiDatabaseQueries
            .getAppPreference("APP_CONFIG")
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map {
                val method = settings.getInt("calc_method", 3)
                val formatStr = settings.getString("time_format", TimeFormat.FORMAT_12H.name)
                val themeStr = settings.getString("app_theme", AppTheme.LIGHT.name)
                val autoLoc = settings.getBoolean("auto_loc", false)

                AppPreferences(
                    calculationMethod = method,
                    timeFormat = TimeFormat.entries.find { it.name == formatStr } ?: TimeFormat.FORMAT_12H,
                    appTheme = AppTheme.entries.find { it.name == themeStr } ?: AppTheme.LIGHT,
                    isAutoLocationEnabled = autoLoc
                )
            }
    }

    override suspend fun updateAppPreferences(preferences: AppPreferences) = withContext(Dispatchers.IO) {
        settings.putInt("calc_method", preferences.calculationMethod)
        settings.putString("time_format", preferences.timeFormat.name)
        settings.putString("app_theme", preferences.appTheme.name)
        settings.putBoolean("auto_loc", preferences.isAutoLocationEnabled)
        database.solatNadiDatabaseQueries.insertOrUpdateAppPreference("APP_CONFIG", "UPDATED")
    }
}
