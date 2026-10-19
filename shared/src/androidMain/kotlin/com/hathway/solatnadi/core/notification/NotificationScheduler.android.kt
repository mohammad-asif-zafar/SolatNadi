package com.hathway.solatnadi.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.shared.R
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

actual class NotificationScheduler(private val context: Context) {

    private var previewMediaPlayer: MediaPlayer? = null

    actual fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("EXTRA_PRAYER_KEY", prayerName.name)
                putExtra("EXTRA_PRAYER_NAME", prayerName.displayName)
            }

            val pendingIntentId = prayerName.ordinal + 100
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val tz = TimeZone.currentSystemDefault()
            var targetInstant = scheduledTime.toInstant(tz)
            @OptIn(kotlin.time.ExperimentalTime::class)
            val nowInstant = kotlin.time.Clock.System.now()

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
            }

            val triggerAtMillis = targetInstant.toEpochMilliseconds()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    putExtra("EXTRA_PRAYER_KEY", prayerName.name)
                    putExtra("EXTRA_PRAYER_NAME", prayerName.displayName)
                }
                val pendingIntentId = prayerName.ordinal + 100
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    pendingIntentId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val tz = TimeZone.currentSystemDefault()
                val triggerAtMillis = scheduledTime.toInstant(tz).toEpochMilliseconds()
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (fallbackEx: Exception) {
                fallbackEx.printStackTrace()
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

    actual fun playAzanPreview() {
        try {
            stopAzanPreview()
            previewMediaPlayer = MediaPlayer.create(context, R.raw.alarm_azan)?.apply {
                start()
                setOnCompletionListener {
                    it.release()
                    if (previewMediaPlayer == it) {
                        previewMediaPlayer = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun stopAzanPreview() {
        try {
            previewMediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            previewMediaPlayer = null
        }
    }
}
