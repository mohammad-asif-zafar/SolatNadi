package com.hathway.solatnadi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.hathway.solatnadi.data.mapper.getCountryName
import com.hathway.solatnadi.data.mapper.toDomain
import com.hathway.solatnadi.data.remote.model.WorldCitiesResponse
import com.hathway.solatnadi.database.SolatNadiDatabase
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.AppTheme
import com.hathway.solatnadi.domain.model.Madhab
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.TimeFormat
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import solatnadi.shared.generated.resources.Res

class PreferencesRepositoryImpl(
    private val database: SolatNadiDatabase,
    private val settings: Settings = Settings()
) : PreferencesRepository {

    private val defaultLocation = PrayerLocation(
        id = "kuala-lumpur",
        name = "Kuala Lumpur",
        country = "Malaysia",
        latitude = 3.1390,
        longitude = 101.6869,
        timezone = "Asia/Kuala_Lumpur",
        isSelected = true
    )

    private val presetLocations = listOf(
        defaultLocation,
        PrayerLocation("singapore", "Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
        PrayerLocation("jakarta", "Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        PrayerLocation("bandar-seri-begawan", "Bandar Seri Begawan", "Brunei", 4.9031, 114.9398, "Asia/Brunei"),
        PrayerLocation("mecca", "Mecca", "Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        PrayerLocation("medina", "Medina", "Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh"),
        PrayerLocation("london", "London", "United Kingdom", 51.5074, -0.1278, "Europe/London"),
        PrayerLocation("penang", "Penang", "Malaysia", 5.4164, 100.3327, "Asia/Kuala_Lumpur"),
        PrayerLocation("johor-bahru", "Johor Bahru", "Malaysia", 1.4927, 103.7414, "Asia/Kuala_Lumpur")
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val queries = database.solatNadiDatabaseQueries
                val existingSelected = queries.selectSelectedLocation().executeAsOneOrNull()
                val currentSelectedId = existingSelected?.id
                    ?: settings.getString("selected_location_id", "kuala-lumpur")

                val bytes = Res.readBytes("files/solatnadi_world_cities.json")
                val jsonStr = bytes.decodeToString()
                val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<WorldCitiesResponse>(jsonStr)

                database.solatNadiDatabaseQueries.transaction {
                    parsed.cities.forEach { city ->
                        val countryName = getCountryName(city.countryCode)
                        val isSel = if (city.id == currentSelectedId || (currentSelectedId == "kuala-lumpur" && city.id == "kl_my")) 1L else 0L
                        queries.insertLocation(
                            id = city.id,
                            name = city.name,
                            country = countryName,
                            latitude = city.latitude,
                            longitude = city.longitude,
                            timezone = city.timezone,
                            isSelected = isSel
                        )
                    }
                }
            } catch (e: Exception) {
                val queries = database.solatNadiDatabaseQueries
                val existingSelected = queries.selectSelectedLocation().executeAsOneOrNull()
                val currentSelectedId = existingSelected?.id
                    ?: settings.getString("selected_location_id", "kuala-lumpur")

                presetLocations.forEach { loc ->
                    queries.insertLocation(
                        id = loc.id,
                        name = loc.name,
                        country = loc.country,
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        timezone = loc.timezone,
                        isSelected = if (loc.id == currentSelectedId) 1L else 0L
                    )
                }
            }
        }
    }

    override fun observeSelectedLocation(): Flow<PrayerLocation> {
        return database.solatNadiDatabaseQueries
            .selectSelectedLocation()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                if (entity != null) {
                    entity.toDomain()
                } else {
                    val savedId = settings.getString("selected_location_id", "kuala-lumpur")
                    val savedEntity = database.solatNadiDatabaseQueries.selectLocationById(savedId).executeAsOneOrNull()
                    savedEntity?.toDomain() ?: defaultLocation
                }
            }
    }

    override suspend fun setSelectedLocation(location: PrayerLocation) = withContext(Dispatchers.IO) {
        settings.putString("selected_location_id", location.id)
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
            soundPreference = "ALARM_AZAN"
        )
    }

    override fun observeAppPreferences(): Flow<AppPreferences> {
        return database.solatNadiDatabaseQueries
            .getAppPreference("APP_CONFIG")
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map {
                val method = settings.getInt("calc_method", 3)
                val madhabStr = settings.getString("madhab", Madhab.SHAFI.name)
                val formatStr = settings.getString("time_format", TimeFormat.FORMAT_12H.name)
                val themeStr = settings.getString("app_theme", AppTheme.LIGHT.name)
                val autoLoc = settings.getBoolean("auto_loc", false)

                AppPreferences(
                    calculationMethod = method,
                    madhab = Madhab.entries.find { it.name == madhabStr } ?: Madhab.SHAFI,
                    timeFormat = TimeFormat.entries.find { it.name == formatStr } ?: TimeFormat.FORMAT_12H,
                    appTheme = AppTheme.entries.find { it.name == themeStr } ?: AppTheme.LIGHT,
                    isAutoLocationEnabled = autoLoc
                )
            }
    }

    override suspend fun updateAppPreferences(preferences: AppPreferences) = withContext(Dispatchers.IO) {
        settings.putInt("calc_method", preferences.calculationMethod)
        settings.putString("madhab", preferences.madhab.name)
        settings.putString("time_format", preferences.timeFormat.name)
        settings.putString("app_theme", preferences.appTheme.name)
        settings.putBoolean("auto_loc", preferences.isAutoLocationEnabled)
        database.solatNadiDatabaseQueries.insertOrUpdateAppPreference("APP_CONFIG", "UPDATED")
    }
}
