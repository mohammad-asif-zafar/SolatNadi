package com.hathway.solatnadi.domain.repository

import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.coroutines.flow.Flow

import com.hathway.solatnadi.domain.model.PrayerNotificationConfig

interface PreferencesRepository {
    fun observeSelectedLocation(): Flow<PrayerLocation>
    suspend fun setSelectedLocation(location: PrayerLocation)
    fun observeAvailableLocations(): Flow<List<PrayerLocation>>
    
    fun observeNotificationPreferences(): Flow<Map<PrayerName, Boolean>>
    suspend fun toggleNotificationPreference(prayerName: PrayerName, enabled: Boolean)

    fun observePrayerNotificationConfigs(): Flow<Map<PrayerName, PrayerNotificationConfig>>
    suspend fun getPrayerNotificationConfig(prayerName: PrayerName): PrayerNotificationConfig
    suspend fun savePrayerNotificationConfig(config: PrayerNotificationConfig)

    fun observeAppPreferences(): Flow<AppPreferences>
    suspend fun updateAppPreferences(preferences: AppPreferences)
}
