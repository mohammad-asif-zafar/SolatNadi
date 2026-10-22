package com.hathway.solatnadi.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.shared.R
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

actual open class NotificationScheduler(private val context: Context) {

    actual constructor() : this(org.koin.core.context.GlobalContext.get().get())

    actual open fun schedulePrayerNotification(prayerName: PrayerName, scheduledTime: LocalDateTime, reminderMinutes: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("EXTRA_PRAYER_KEY", prayerName.name)
                putExtra("EXTRA_PRAYER_NAME", prayerName.displayName)
                putExtra("EXTRA_REMINDER_MINUTES", reminderMinutes)
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
            if (reminderMinutes > 0) {
                targetInstant = targetInstant.minus(reminderMinutes.minutes)
            }

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
                if (reminderMinutes > 0) {
                    targetInstant = targetInstant.minus(reminderMinutes.minutes)
                }
            }

            val triggerAtMillis = targetInstant.toEpochMilliseconds()

            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val showPendingIntent = if (launchIntent != null) {
                PendingIntent.getActivity(
                    context,
                    pendingIntentId + 500,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                pendingIntent
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
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
                    putExtra("EXTRA_REMINDER_MINUTES", reminderMinutes)
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

    actual open fun cancelPrayerNotification(prayerName: PrayerName) {
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

    actual open fun playAzanPreview(soundKey: String) {
        try {
            if (soundKey == "silent") {
                AudioPlayerManager.stopSound()
                return
            }
            val rawRes = getRawResourceForSoundKey(soundKey)
            AudioPlayerManager.playSound(context, rawRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual open fun stopAzanPreview() {
        AudioPlayerManager.stopSound()
    }

    actual open fun triggerTestNotification(prayerName: PrayerName) {
        try {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("EXTRA_PRAYER_KEY", prayerName.name)
                putExtra("EXTRA_PRAYER_NAME", prayerName.displayName)
                putExtra("EXTRA_TEST_NOTIFICATION", true)
            }
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRawResourceForSoundKey(soundKey: String): Int {
        return when (soundKey) {
            "allahu_akbar" -> R.raw.allahu_akbar
            "makkah_adhan", "default_adhan" -> R.raw.adhan_mecca
            "oman_adhan" -> R.raw.oman_adhan
            "qatar_adhan" -> R.raw.qatar_adhan
            "egypt_fajr_adhan" -> R.raw.egypt_fajr_adhan
            "lebanon_adhan" -> R.raw.lebanon_adhan
            "alarm_azan" -> R.raw.alarm_azan
            else -> R.raw.adhan_mecca
        }
    }
}
