package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.core.notification.NotificationScheduler
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import com.hathway.solatnadi.domain.model.PrayerNotificationConfig

import kotlinx.coroutines.flow.flowOf

class FakeTogglePreferencesRepository : PreferencesRepository {
    val notificationMap = mutableMapOf<PrayerName, Boolean>()
    val configMap = mutableMapOf<PrayerName, PrayerNotificationConfig>()

    override fun observeSelectedLocation(): Flow<PrayerLocation> = error("Not used")
    override suspend fun setSelectedLocation(location: PrayerLocation) {}
    override fun observeAvailableLocations(): Flow<List<PrayerLocation>> = error("Not used")
    override fun observeNotificationPreferences(): Flow<Map<PrayerName, Boolean>> = flowOf(notificationMap)

    override suspend fun toggleNotificationPreference(prayerName: PrayerName, enabled: Boolean) {
        notificationMap[prayerName] = enabled
        configMap[prayerName] = (configMap[prayerName] ?: PrayerNotificationConfig(prayerName)).copy(enabled = enabled)
    }

    override fun observePrayerNotificationConfigs(): Flow<Map<PrayerName, PrayerNotificationConfig>> = flowOf(configMap)

    override suspend fun getPrayerNotificationConfig(prayerName: PrayerName): PrayerNotificationConfig {
        return configMap[prayerName] ?: PrayerNotificationConfig(prayerName, enabled = notificationMap[prayerName] ?: true)
    }

    override suspend fun savePrayerNotificationConfig(config: PrayerNotificationConfig) {
        configMap[config.prayerName] = config
        notificationMap[config.prayerName] = config.enabled
    }

    override fun observeAppPreferences(): Flow<AppPreferences> = error("Not used")
    override suspend fun updateAppPreferences(preferences: AppPreferences) {}
}

class TestToggleNotificationScheduler : NotificationScheduler() {
    val scheduledList = mutableListOf<Pair<PrayerName, LocalDateTime>>()
    val cancelledList = mutableListOf<PrayerName>()
    var playPreviewCallCount = 0
    var stopPreviewCallCount = 0

    override fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int) {
        scheduledList.add(prayerName to scheduledTime)
    }

    override fun cancelPrayerNotification(prayerName: PrayerName) {
        cancelledList.add(prayerName)
    }

    override fun playAzanPreview(soundKey: String) {
        playPreviewCallCount++
    }

    override fun stopAzanPreview() {
        stopPreviewCallCount++
    }

    override fun triggerTestNotification(prayerName: PrayerName) {
    }
}

class ToggleNotificationUseCaseTest {

    private val repository = FakeTogglePreferencesRepository()
    private val scheduler = TestToggleNotificationScheduler()
    private val useCase = ToggleNotificationUseCase(repository, scheduler)

    @Test
    fun testToggleOnSchedulesAlarmAndSavesPreferenceWithoutPlayingAudioPreview() = runTest {
        val prayerTime = PrayerTime(
            prayerName = PrayerName.FAJR,
            timeString = "05:54 AM",
            scheduledDateTime = LocalDateTime(2026, 10, 19, 5, 54, 0),
            isNotificationEnabled = false,
            isNext = true
        )

        useCase(prayerTime, enabled = true)

        // 1. Check preference is saved as true
        assertEquals(true, repository.notificationMap[PrayerName.FAJR])

        // 2. Check alarm is scheduled for exact prayer time
        assertEquals(1, scheduler.scheduledList.size)
        assertEquals(PrayerName.FAJR, scheduler.scheduledList[0].first)
        assertEquals(LocalDateTime(2026, 10, 19, 5, 54, 0), scheduler.scheduledList[0].second)

        // 3. CRITICAL: Verify NO audio preview is played on toggle (plays at prayer time only!)
        assertEquals(0, scheduler.playPreviewCallCount)
    }

    @Test
    fun testToggleOffCancelsAlarmAndSavesPreferenceWithoutPlayingAudioPreview() = runTest {
        val prayerTime = PrayerTime(
            prayerName = PrayerName.MAGHRIB,
            timeString = "07:21 PM",
            scheduledDateTime = LocalDateTime(2026, 10, 19, 19, 21, 0),
            isNotificationEnabled = true,
            isNext = false
        )

        useCase(prayerTime, enabled = false)

        // 1. Check preference is saved as false
        assertEquals(false, repository.notificationMap[PrayerName.MAGHRIB])

        // 2. Check alarm is cancelled
        assertEquals(1, scheduler.cancelledList.size)
        assertEquals(PrayerName.MAGHRIB, scheduler.cancelledList[0])

        // 3. CRITICAL: Verify NO audio preview was triggered
        assertEquals(0, scheduler.playPreviewCallCount)
    }

    @Test
    fun testToggleOffByPrayerNameCancelsAlarmAndSavesPreference() = runTest {
        useCase(PrayerName.ISHA, enabled = false)

        assertEquals(false, repository.notificationMap[PrayerName.ISHA])
        assertEquals(1, scheduler.cancelledList.size)
        assertEquals(PrayerName.ISHA, scheduler.cancelledList[0])
        assertEquals(0, scheduler.playPreviewCallCount)
    }
}
