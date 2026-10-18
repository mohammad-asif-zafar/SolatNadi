package com.hathway.solatnadi.domain.repository

import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeSelectedLocation(): Flow<PrayerLocation>
    suspend fun setSelectedLocation(location: PrayerLocation)
    fun observeAvailableLocations(): Flow<List<PrayerLocation>>
    
    fun observeNotificationPreferences(): Flow<Map<PrayerName, Boolean>>
    suspend fun toggleNotificationPreference(prayerName: PrayerName, enabled: Boolean)

    fun observeAppPreferences(): Flow<AppPreferences>
    suspend fun updateAppPreferences(preferences: AppPreferences)
}
