package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

expect open class NotificationScheduler() {
    open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int = 10)
    open fun cancelPrayerNotification(prayerName: PrayerName)
    open fun playAzanPreview(soundKey: String = "default_adhan")
    open fun stopAzanPreview()
    open fun triggerTestNotification(prayerName: PrayerName = PrayerName.FAJR)
}
