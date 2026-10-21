package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PrayerNotificationUseCasesTest {

    private val repository = FakePreferencesRepository()
    private val scheduler = TestNotificationScheduler()

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
        assertTrue(map.containsKey(PrayerName.FAJR))
        assertTrue(map.containsKey(PrayerName.DHUHR))
        assertTrue(map.containsKey(PrayerName.ASR))
        assertTrue(map.containsKey(PrayerName.MAGHRIB))
        assertTrue(map.containsKey(PrayerName.ISHA))
    }
}
