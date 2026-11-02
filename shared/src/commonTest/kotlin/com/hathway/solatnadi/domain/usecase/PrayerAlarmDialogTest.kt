package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.core.notification.AlarmStateManager
import com.hathway.solatnadi.core.notification.AudioLifecycleCoordinator
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrayerAlarmDialogTest {

    private val scheduler = TestToggleNotificationScheduler()
    private val coordinator = AudioLifecycleCoordinator()

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
    fun testAlarmStateManagerTriggerAndClear() {
        AlarmStateManager.clearActiveAlarm()
        assertNull(AlarmStateManager.activeAlarm.value)

        val alarm = ActiveAlarmData(PrayerName.ISHA, "08:30 PM")
        AlarmStateManager.triggerAlarm(alarm)
        assertEquals(alarm, AlarmStateManager.activeAlarm.value)

        AlarmStateManager.clearActiveAlarm()
        assertNull(AlarmStateManager.activeAlarm.value)
    }

    @Test
    fun testNewSessionInitializationAndIncrement() {
        assertEquals(0L, coordinator.sessionId)
        assertFalse(coordinator.isPlaying)

        val session1 = coordinator.newSession()
        assertEquals(1L, session1)
        assertEquals(1L, coordinator.sessionId)
        assertTrue(coordinator.isPlaying)
        assertFalse(coordinator.isStaleSession(session1))

        val session2 = coordinator.newSession()
        assertEquals(2L, session2)
        assertEquals(2L, coordinator.sessionId)
        assertTrue(coordinator.isPlaying)
        assertTrue(coordinator.isStaleSession(session1))
        assertFalse(coordinator.isStaleSession(session2))

        coordinator.explicitDismissal()
    }

    @Test
    fun testPlaybackStartMaintainsActiveAlarm() = runTest {
        val alarm = ActiveAlarmData(PrayerName.FAJR, "05:10 AM")
        coordinator.startPlayback(alarm)

        assertTrue(coordinator.isPlaying)
        assertNotNull(AlarmStateManager.activeAlarm.value)
        assertEquals(alarm, AlarmStateManager.activeAlarm.value)

        coordinator.explicitDismissal()
    }

    @Test
    fun testNaturalCompletionDoesNotDismissActiveAlarm() = runTest {
        val alarm = ActiveAlarmData(PrayerName.DHUHR, "12:15 PM")
        val session = coordinator.startPlayback(alarm)

        coordinator.handleNaturalCompletion(session)

        assertFalse(coordinator.isPlaying)
        assertNotNull(AlarmStateManager.activeAlarm.value)
        assertEquals(alarm, AlarmStateManager.activeAlarm.value)

        coordinator.explicitDismissal()
    }

    @Test
    fun testPlaybackFailureDoesNotDismissActiveAlarm() = runTest {
        val alarm = ActiveAlarmData(PrayerName.ASR, "03:30 PM")
        val session = coordinator.startPlayback(alarm)

        coordinator.handlePlaybackFailure(session)

        assertFalse(coordinator.isPlaying)
        assertNotNull(AlarmStateManager.activeAlarm.value)
        assertEquals(alarm, AlarmStateManager.activeAlarm.value)

        coordinator.explicitDismissal()
    }

    @Test
    fun testOldSessionCompletionAndFailureCannotAffectNewerSession() {
        val session1 = coordinator.newSession()
        assertTrue(coordinator.isPlaying)

        val session2 = coordinator.newSession()
        assertTrue(coordinator.isPlaying)

        // Old session 1 completion or failure arrival should be ignored due to staleness check
        coordinator.handleNaturalCompletion(session1)
        assertTrue(coordinator.isPlaying)

        coordinator.handlePlaybackFailure(session1)
        assertTrue(coordinator.isPlaying)

        // Current session 2 completion updates state correctly
        coordinator.handleNaturalCompletion(session2)
        assertFalse(coordinator.isPlaying)

        coordinator.explicitDismissal()
    }

    @Test
    fun testExplicitDismissalClearsActiveAlarmAndInvalidatesSession() = runTest {
        val alarm = ActiveAlarmData(PrayerName.DHUHR, "01:12 PM")
        val session1 = coordinator.startPlayback(alarm)
        assertNotNull(AlarmStateManager.activeAlarm.value)
        assertTrue(coordinator.isPlaying)

        coordinator.explicitDismissal()

        assertFalse(coordinator.isPlaying)
        assertNull(AlarmStateManager.activeAlarm.value)
        // Session 1 is now invalidated (session ID incremented)
        assertTrue(coordinator.isStaleSession(session1))
    }

    @Test
    fun testSnoozeStopsPlaybackSchedulesSnoozeAndClearsAlarm() = runTest {
        val alarm = ActiveAlarmData(PrayerName.ASR, "04:25 PM")
        val session1 = coordinator.startPlayback(alarm)
        val snoozeTime = LocalDateTime(2026, 10, 19, 16, 30, 0)

        var snoozeScheduled = false
        coordinator.snooze {
            scheduler.schedulePrayerNotification(PrayerName.ASR, snoozeTime, reminderMinutes = 0)
            snoozeScheduled = true
        }

        assertFalse(coordinator.isPlaying)
        assertTrue(snoozeScheduled)
        assertEquals(1, scheduler.scheduledList.size)
        assertEquals(PrayerName.ASR, scheduler.scheduledList[0].first)
        assertNull(AlarmStateManager.activeAlarm.value)
        assertTrue(coordinator.isStaleSession(session1))
    }

    @Test
    fun testRepeatedCompletionCallbackHandledOnlyOnce() {
        val session = coordinator.newSession()
        assertTrue(coordinator.isPlaying)

        coordinator.handleNaturalCompletion(session)
        assertFalse(coordinator.isPlaying)

        // Second completion callback for same session has no adverse effect
        coordinator.handleNaturalCompletion(session)
        assertFalse(coordinator.isPlaying)

        coordinator.explicitDismissal()
    }

    @Test
    fun testDeviceModeVibrationMatrixLogic() {
        fun computeEffectiveVibration(ringerModeSilent: Boolean, appVibrationPreference: Boolean): Boolean {
            return if (ringerModeSilent) false else appVibrationPreference
        }

        // Silent mode -> Vibration OFF regardless of preference
        assertFalse(computeEffectiveVibration(ringerModeSilent = true, appVibrationPreference = true))
        assertFalse(computeEffectiveVibration(ringerModeSilent = true, appVibrationPreference = false))

        // Normal / Vibrate mode -> Follows app preference
        assertTrue(computeEffectiveVibration(ringerModeSilent = false, appVibrationPreference = true))
        assertFalse(computeEffectiveVibration(ringerModeSilent = false, appVibrationPreference = false))
    }
}
