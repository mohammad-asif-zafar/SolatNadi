package com.hathway.solatnadi.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

actual class NotificationScheduler(private val context: Context) {

    actual fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("EXTRA_PRAYER_NAME", prayerName.displayName)
        }

        val pendingIntentId = prayerName.ordinal + 100
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pendingIntentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = scheduledTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

        if (triggerAtMillis > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        }
    }

    actual fun cancelPrayerNotification(prayerName: PrayerName) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerAlarmReceiver::class.java)
        val pendingIntentId = prayerName.ordinal + 100
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pendingIntentId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
