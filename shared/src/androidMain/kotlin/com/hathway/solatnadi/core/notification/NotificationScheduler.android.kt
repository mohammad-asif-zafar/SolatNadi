package com.hathway.solatnadi.core.notification

import android.content.Context
import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime

actual class NotificationScheduler(private val context: Context) {
    actual fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime) {
    }

    actual fun cancelPrayerNotification(prayerName: PrayerName) {
    }
}
