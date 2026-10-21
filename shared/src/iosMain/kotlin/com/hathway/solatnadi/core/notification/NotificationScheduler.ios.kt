package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

actual open class NotificationScheduler actual constructor() {

    actual open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int) {
    }

    actual open fun cancelPrayerNotification(prayerName: PrayerName) {
    }

    actual open fun playAzanPreview(soundKey: String) {
    }

    actual open fun stopAzanPreview() {
    }

    actual open fun triggerTestNotification(prayerName: PrayerName) {
    }
}
