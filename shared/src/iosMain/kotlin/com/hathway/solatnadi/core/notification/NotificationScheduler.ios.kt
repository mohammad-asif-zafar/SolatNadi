package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.data.mapper.formatTo12Hour
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

actual open class NotificationScheduler actual constructor() {

    init {
        setupNotificationCenterDelegate()
    }

    private fun setupNotificationCenterDelegate() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        if (center.delegate == null) {
            center.delegate = SolatNadiNotificationDelegate()
        }
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, error ->
            if (error != null) {
                println("SolatNadi Notification Permission Error: ${error.localizedDescription}")
            }
        }
    }

    actual open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val tz = TimeZone.currentSystemDefault()
        var targetInstant = scheduledTime.toInstant(tz)
        if (reminderMinutes > 0) {
            targetInstant = targetInstant.minus(reminderMinutes.minutes)
        }

        val nowInstant = Clock.System.now()
        if (targetInstant <= nowInstant) {
            val tomorrowDate = scheduledTime.date.plus(1, DateTimeUnit.DAY)
            val tomorrowDateTime = LocalDateTime(
                tomorrowDate.year,
                tomorrowDate.month,
                tomorrowDate.day,
                scheduledTime.hour,
                scheduledTime.minute,
                scheduledTime.second
            )
            targetInstant = tomorrowDateTime.toInstant(tz)
            if (reminderMinutes > 0) {
                targetInstant = targetInstant.minus(reminderMinutes.minutes)
            }
        }

        val targetDateTime = targetInstant.toLocalDateTime(tz)

        val content = UNMutableNotificationContent().apply {
            setTitle("It's time for ${prayerName.displayName}")
            setBody("Let's prepare for prayer 💚")
            setUserInfo(
                mapOf(
                    "EXTRA_PRAYER_KEY" to prayerName.name,
                    "EXTRA_PRAYER_NAME" to prayerName.displayName
                )
            )
            setSound(UNNotificationSound.defaultSound)
        }

        val dateComponents = NSDateComponents().apply {
            setYear(targetDateTime.year.toLong())
            setMonth((targetDateTime.month.ordinal + 1).toLong())
            setDay(targetDateTime.day.toLong())
            setHour(targetDateTime.hour.toLong())
            setMinute(targetDateTime.minute.toLong())
            setSecond(0)
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = false
        )

        val identifier = "solatnadi_prayer_${prayerName.name}"
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = identifier,
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("SolatNadi Failed to schedule iOS notification for ${prayerName.name}: ${error.localizedDescription}")
            }
        }
    }

    actual open fun cancelPrayerNotification(prayerName: PrayerName) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val identifier = "solatnadi_prayer_${prayerName.name}"
        center.removePendingNotificationRequestsWithIdentifiers(listOf(identifier))
    }

    actual open fun playAzanPreview(soundKey: String) {
        IosAudioPlayerManager.playSound(soundKey)
    }

    actual open fun stopAzanPreview() {
        IosAudioPlayerManager.stopSound()
    }

    actual open fun triggerTestNotification(prayerName: PrayerName) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val content = UNMutableNotificationContent().apply {
            setTitle("It's time for ${prayerName.displayName}")
            setBody("Let's prepare for prayer 💚")
            setUserInfo(
                mapOf(
                    "EXTRA_PRAYER_KEY" to prayerName.name,
                    "EXTRA_PRAYER_NAME" to prayerName.displayName
                )
            )
            setSound(UNNotificationSound.defaultSound)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false
        )

        val identifier = "solatnadi_test_notification_${Clock.System.now().toEpochMilliseconds()}"
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = identifier,
            content = content,
            trigger = trigger
        )

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedTime = formatTo12Hour(now.hour, now.minute)
        AlarmStateManager.triggerAlarm(
            ActiveAlarmData(
                prayerName = prayerName,
                timeFormatted = formattedTime
            )
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("SolatNadi Failed to trigger iOS test notification: ${error.localizedDescription}")
            }
        }
    }
}

private class SolatNadiNotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (ULong) -> Unit
    ) {
        val userInfo = willPresentNotification.request.content.userInfo
        val prayerKey = userInfo["EXTRA_PRAYER_KEY"] as? String ?: "FAJR"
        val prayerDisplayName = userInfo["EXTRA_PRAYER_NAME"] as? String ?: "Prayer"

        val prayerNameEnum = PrayerName.entries.firstOrNull {
            it.name.equals(prayerKey, ignoreCase = true) || it.displayName.equals(prayerDisplayName, ignoreCase = true)
        } ?: PrayerName.FAJR

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedTime = formatTo12Hour(now.hour, now.minute)

        AlarmStateManager.triggerAlarm(
            ActiveAlarmData(
                prayerName = prayerNameEnum,
                timeFormatted = formattedTime
            )
        )

        withCompletionHandler(UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound)
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        val userInfo = didReceiveNotificationResponse.notification.request.content.userInfo
        val prayerKey = userInfo["EXTRA_PRAYER_KEY"] as? String ?: "FAJR"
        val prayerDisplayName = userInfo["EXTRA_PRAYER_NAME"] as? String ?: "Prayer"

        val prayerNameEnum = PrayerName.entries.firstOrNull {
            it.name.equals(prayerKey, ignoreCase = true) || it.displayName.equals(prayerDisplayName, ignoreCase = true)
        } ?: PrayerName.FAJR

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedTime = formatTo12Hour(now.hour, now.minute)

        AlarmStateManager.triggerAlarm(
            ActiveAlarmData(
                prayerName = prayerNameEnum,
                timeFormatted = formattedTime
            )
        )

        withCompletionHandler()
    }
}
