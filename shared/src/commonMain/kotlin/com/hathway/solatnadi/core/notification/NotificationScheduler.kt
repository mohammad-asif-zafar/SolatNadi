package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

expect open class NotificationScheduler() {
    open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime)
    open fun cancelPrayerNotification(prayerName: PrayerName)
    open fun playAzanPreview()
    open fun stopAzanPreview()
}
