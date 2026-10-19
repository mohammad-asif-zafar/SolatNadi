package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

actual open class NotificationScheduler {
    actual open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime) {
    }

    actual open fun cancelPrayerNotification(prayerName: PrayerName) {
    }

    actual open fun playAzanPreview() {
    }

    actual open fun stopAzanPreview() {
    }
}
