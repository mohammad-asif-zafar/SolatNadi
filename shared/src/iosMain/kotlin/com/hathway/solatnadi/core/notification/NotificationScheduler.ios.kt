package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

actual class NotificationScheduler {
    actual fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime) {
    }

    actual fun cancelPrayerNotification(prayerName: PrayerName) {
    }

    actual fun playAzanPreview() {
    }

    actual fun stopAzanPreview() {
    }
}
