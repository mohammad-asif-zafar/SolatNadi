package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

expect class NotificationScheduler {
    fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime)
    fun cancelPrayerNotification(prayerName: PrayerName)
    fun playAzanPreview()
    fun stopAzanPreview()
}
