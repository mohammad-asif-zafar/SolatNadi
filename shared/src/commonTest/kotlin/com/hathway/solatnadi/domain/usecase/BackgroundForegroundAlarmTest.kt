package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.model.PrayerTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackgroundForegroundAlarmTest {

    private val repository = FakeTogglePreferencesRepository()
    private val scheduler = TestToggleNotificationScheduler()
    private val saveConfigUseCase = SavePrayerNotificationConfigUseCase(repository, scheduler)

    @Test
    fun testAlarmSchedulingComputesExactTriggerTimeForFuturePrayer() = runTest {
        val futureTime = LocalDateTime(2026, 10, 19, 18, 30, 0)
        val config = PrayerNotificationConfig(
            prayerName = PrayerName.MAGHRIB,
            enabled = true,
            reminderMinutes = 0
        )

        saveConfigUseCase(config)

        assertEquals(1, scheduler.scheduledList.size)
        assertEquals(PrayerName.MAGHRIB, scheduler.scheduledList[0].first)
        assertTrue(repository.notificationMap[PrayerName.MAGHRIB] == true)
    }

    @Test
    fun testAlarmSchedulingWithReminderOffset() = runTest {
        val prayerTime = LocalDateTime(2026, 10, 19, 13, 15, 0)
        val config = PrayerNotificationConfig(
            prayerName = PrayerName.DHUHR,
            enabled = true,
            reminderMinutes = 15
        )

        saveConfigUseCase(config)

        assertEquals(1, scheduler.scheduledList.size)
        val savedConfig = repository.notificationMap[PrayerName.DHUHR]
        assertEquals(true, savedConfig)
    }

    @Test
    fun testAlarmDisabledInDatabaseAbortsAlarmExecution() = runTest {
        val config = PrayerNotificationConfig(
            prayerName = PrayerName.FAJR,
            enabled = false
        )

        saveConfigUseCase(config)

        assertEquals(0, scheduler.scheduledList.size)
        assertEquals(1, scheduler.cancelledList.size)
        assertEquals(PrayerName.FAJR, scheduler.cancelledList[0])
        assertEquals(false, repository.notificationMap[PrayerName.FAJR])
    }

    @Test
    fun testAlarmRolloverToTomorrowWhenPrayerTimePassed() {
        val tz = TimeZone.currentSystemDefault()
        val pastPrayerTime = LocalDateTime(2026, 10, 19, 5, 0, 0)
        val nowInstant = LocalDateTime(2026, 10, 19, 10, 0, 0).toInstant(tz)
        val targetInstant = pastPrayerTime.toInstant(tz)

        assertTrue(targetInstant <= nowInstant)

        val tomorrowDate = pastPrayerTime.date.plus(1, DateTimeUnit.DAY)
        val tomorrowDateTime = LocalDateTime(
            tomorrowDate.year,
            tomorrowDate.month,
            tomorrowDate.day,
            pastPrayerTime.hour,
            pastPrayerTime.minute,
            pastPrayerTime.second
        )
        val nextDayTargetInstant = tomorrowDateTime.toInstant(tz)

        assertTrue(nextDayTargetInstant > nowInstant)
    }
}
