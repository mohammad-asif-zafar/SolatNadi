package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.core.notification.NotificationScheduler
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import kotlinx.coroutines.flow.flowOf

class FakeNotificationUseCaseRepo : PreferencesRepository {
    val notificationMap = mutableMapOf<PrayerName, Boolean>()
    val configMap = mutableMapOf<PrayerName, PrayerNotificationConfig>()

    override fun observeSelectedLocation(): Flow<PrayerLocation> = error("Not used")
    override suspend fun setSelectedLocation(location: PrayerLocation) {}
    override fun observeAvailableLocations(): Flow<List<PrayerLocation>> = error("Not used")
    override fun observeNotificationPreferences(): Flow<Map<PrayerName, Boolean>> = flowOf(notificationMap)

    override suspend fun toggleNotificationPreference(prayerName: PrayerName, enabled: Boolean) {
        notificationMap[prayerName] = enabled
    }

    override fun observePrayerNotificationConfigs(): Flow<Map<PrayerName, PrayerNotificationConfig>> {
        val fullMap = PrayerName.entries.associateWith { name -> configMap[name] ?: PrayerNotificationConfig(name) }
        return flowOf(fullMap)
    }

    override suspend fun getPrayerNotificationConfig(prayerName: PrayerName): PrayerNotificationConfig {
        return configMap[prayerName] ?: PrayerNotificationConfig(prayerName)
    }

    override suspend fun savePrayerNotificationConfig(config: PrayerNotificationConfig) {
        configMap[config.prayerName] = config
        notificationMap[config.prayerName] = config.enabled
    }

    override fun observeAppPreferences(): Flow<AppPreferences> = error("Not used")
    override suspend fun updateAppPreferences(preferences: AppPreferences) {}
}

class TestNotificationUseCaseScheduler : NotificationScheduler() {
    val scheduledList = mutableListOf<Pair<PrayerName, LocalDateTime>>()
    val cancelledList = mutableListOf<PrayerName>()

    override fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int) {
        scheduledList.add(prayerName to scheduledTime)
    }

    override fun cancelPrayerNotification(prayerName: PrayerName) {
        cancelledList.add(prayerName)
    }

    override fun playAzanPreview(soundKey: String) {}
    override fun stopAzanPreview() {}
    override fun triggerTestNotification(prayerName: PrayerName) {}
}

class PrayerNotificationUseCasesTest {

    private val repository = FakeNotificationUseCaseRepo()
    private val scheduler = TestNotificationUseCaseScheduler()

    private val observeConfigsUseCase = ObservePrayerNotificationConfigsUseCase(repository)
    private val getConfigUseCase = GetPrayerNotificationConfigUseCase(repository)
    private val saveConfigUseCase = SavePrayerNotificationConfigUseCase(repository, scheduler)

    @Test
    fun testGetPrayerNotificationConfigReturnsDefaultFallbackWhenEmpty() = runTest {
        val config = getConfigUseCase(PrayerName.FAJR)

        assertNotNull(config)
        assertEquals(PrayerName.FAJR, config.prayerName)
        assertTrue(config.enabled)
        assertEquals(10, config.reminderMinutes)
        assertEquals("default_adhan", config.soundKey)
        assertTrue(config.vibrationEnabled)
        assertFalse(config.repeatReminderEnabled)
    }

    @Test
    fun testSavePrayerNotificationConfigSchedulesAlarmWhenEnabled() = runTest {
        val customConfig = PrayerNotificationConfig(
            prayerName = PrayerName.DHUHR,
            enabled = true,
            reminderMinutes = 15,
            soundKey = "madinah_adhan",
            vibrationEnabled = true,
            repeatReminderEnabled = true
        )

        saveConfigUseCase(customConfig)

        // Verify config is saved in repository
        val saved = getConfigUseCase(PrayerName.DHUHR)
        assertEquals(15, saved.reminderMinutes)
        assertEquals("madinah_adhan", saved.soundKey)
        assertTrue(saved.repeatReminderEnabled)

        // Verify exact alarm scheduling was called
        assertEquals(1, scheduler.scheduledList.size)
        assertEquals(PrayerName.DHUHR, scheduler.scheduledList[0].first)
    }

    @Test
    fun testSavePrayerNotificationConfigCancelsAlarmWhenDisabled() = runTest {
        val disabledConfig = PrayerNotificationConfig(
            prayerName = PrayerName.ASR,
            enabled = false
        )

        saveConfigUseCase(disabledConfig)

        val saved = getConfigUseCase(PrayerName.ASR)
        assertFalse(saved.enabled)

        // Verify cancel was called
        assertEquals(1, scheduler.cancelledList.size)
        assertEquals(PrayerName.ASR, scheduler.cancelledList[0])
    }

    @Test
    fun testObservePrayerNotificationConfigsEmitsAllPrayers() = runTest {
        val map = observeConfigsUseCase().first()

        assertNotNull(map)
        assertEquals(5, map.size)
        assertNotNull(map[PrayerName.FAJR])
        assertNotNull(map[PrayerName.DHUHR])
        assertNotNull(map[PrayerName.ASR])
        assertNotNull(map[PrayerName.MAGHRIB])
        assertNotNull(map[PrayerName.ISHA])
    }
}
