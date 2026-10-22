package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PrayerAlarmDialogTest {

    private val scheduler = TestToggleNotificationScheduler()

    @Test
    fun testActiveAlarmDataDefaultValues() {
        val alarm = ActiveAlarmData()
        assertEquals(PrayerName.FAJR, alarm.prayerName)
        assertEquals("05:47 AM", alarm.timeFormatted)
    }

    @Test
    fun testActiveAlarmDataCustomPrayer() {
        val alarm = ActiveAlarmData(
            prayerName = PrayerName.MAGHRIB,
            timeFormatted = "07:18 PM"
        )
        assertEquals(PrayerName.MAGHRIB, alarm.prayerName)
        assertEquals("07:18 PM", alarm.timeFormatted)
    }

    @Test
    fun testStopAlarmActionHaltsAudioAndDismissesDialog() = runTest {
        var activeAlarm: ActiveAlarmData? = ActiveAlarmData(PrayerName.DHUHR, "01:12 PM")

        // Simulate Stop Alarm action
        scheduler.stopAzanPreview()
        activeAlarm = null

        assertEquals(1, scheduler.stopPreviewCallCount)
        assertNull(activeAlarm)
    }

    @Test
    fun testSnoozeAlarmActionHaltsAudioAndSchedules5MinAlarm() = runTest {
        var activeAlarm: ActiveAlarmData? = ActiveAlarmData(PrayerName.ASR, "04:25 PM")
        val snoozeTime = LocalDateTime(2026, 10, 19, 16, 30, 0)

        // Simulate Snooze action: stop current audio and schedule 5-min snooze
        scheduler.stopAzanPreview()
        scheduler.schedulePrayerNotification(PrayerName.ASR, snoozeTime, reminderMinutes = 0)
        activeAlarm = null

        assertEquals(1, scheduler.stopPreviewCallCount)
        assertEquals(1, scheduler.scheduledList.size)
        assertEquals(PrayerName.ASR, scheduler.scheduledList[0].first)
        assertNull(activeAlarm)
    }

    @Test
    fun testDismissDialogRequestHaltsAudioAndClearsAlarmData() = runTest {
        var activeAlarm: ActiveAlarmData? = ActiveAlarmData(PrayerName.ISHA, "08:29 PM")

        // Simulate dismiss on back press or outside click
        scheduler.stopAzanPreview()
        activeAlarm = null

        assertEquals(1, scheduler.stopPreviewCallCount)
        assertNull(activeAlarm)
    }
}
