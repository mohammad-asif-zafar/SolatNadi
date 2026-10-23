package com.hathway.solatnadi.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.core.database.createDatabase
import com.hathway.solatnadi.data.mapper.formatTo12Hour
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import com.hathway.solatnadi.shared.R
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SolatNadi:AzanAlarmWakeLock")
        // Acquire for 5 minutes max to ensure full Adhan plays
        wakeLock?.acquire(5 * 60 * 1000L)

        val prayerKey = intent.getStringExtra("EXTRA_PRAYER_KEY") ?: intent.getStringExtra("EXTRA_PRAYER_NAME")?.uppercase() ?: "FAJR"
        val prayerDisplayName = intent.getStringExtra("EXTRA_PRAYER_NAME") ?: "Prayer"

        val isTestNotification = intent.getBooleanExtra("EXTRA_TEST_NOTIFICATION", false)

        var cityName = ""
        var soundKey = "default_adhan"
        var vibrationEnabled = false
        try {
            val database = createDatabase(DatabaseDriverFactory(context))
            val pref = database.solatNadiDatabaseQueries
                .getNotificationPreferenceForPrayer(prayerKey)
                .executeAsOneOrNull()

            if (!isTestNotification && pref != null && pref.enabled == 0L) {
                if (wakeLock?.isHeld == true) {
                    try { wakeLock.release() } catch (_: Exception) {}
                }
                return
            }

            if (pref != null) {
                soundKey = pref.soundPreference
                vibrationEnabled = pref.vibrationEnabled == 1L
            }

            val selectedLocation = database.solatNadiDatabaseQueries
                .selectSelectedLocation()
                .executeAsOneOrNull()
            if (selectedLocation != null) {
                cityName = selectedLocation.name
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val rawSoundRes = getRawResourceForSoundKey(soundKey)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val prayerNameEnum = PrayerName.entries.firstOrNull {
            it.name.equals(prayerKey, ignoreCase = true) || it.displayName.equals(prayerDisplayName, ignoreCase = true)
        } ?: PrayerName.FAJR

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val formattedTime = formatTo12Hour(now.hour, now.minute)

        AlarmStateManager.triggerAlarm(
            ActiveAlarmData(
                prayerName = prayerNameEnum,
                timeFormatted = formattedTime
            )
        )

        // Automatically reschedule next day's alarm for this prayer
        if (!isTestNotification) {
            try {
                val reminderMinutes = intent.getIntExtra("EXTRA_REMINDER_MINUTES", 0)
                NotificationScheduler(context).schedulePrayerNotification(
                    prayerName = prayerNameEnum,
                    scheduledTime = now,
                    reminderMinutes = reminderMinutes
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Create a new channel ID version to ensure previous channel sound and vibration settings are overridden
        val channelId = if (soundKey == "silent") "solatnadi_silent_channel_v3" else "solatnadi_prayer_channel_v3_${soundKey}_vib_${vibrationEnabled}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                if (soundKey == "silent") "Silent Prayer Notifications" else "Prayer Time Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Islamic prayer times"
                enableVibration(vibrationEnabled)
                if (!vibrationEnabled) {
                    vibrationPattern = longArrayOf(0L)
                }
                // We use AudioPlayerManager to play the sound, so the system notification should be silent.
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        if (soundKey != "silent") {
            AudioPlayerManager.playSound(context, rawSoundRes) {
                if (wakeLock?.isHeld == true) {
                    try { wakeLock.release() } catch (_: Exception) {}
                }
                AlarmStateManager.clearActiveAlarm()
            }
        } else {
            if (wakeLock?.isHeld == true) {
                try { wakeLock.release() } catch (_: Exception) {}
            }
        }

        val titleText = "It's time for $prayerDisplayName"
        val contentText = "Let's prepare for prayer 💚"
        val subText = if (cityName.isNotEmpty()) cityName else "SolatNadi"

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_PRAYER_KEY", prayerKey)
            putExtra("EXTRA_PRAYER_NAME", prayerDisplayName)
        }
        val pendingContentIntent = launchIntent?.let {
            android.app.PendingIntent.getActivity(
                context,
                prayerKey.hashCode(),
                it,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setSubText(subText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        if (!vibrationEnabled) {
            notificationBuilder.setVibrate(longArrayOf(0L))
        }

        if (pendingContentIntent != null) {
            notificationBuilder.setContentIntent(pendingContentIntent)
        }

        val notificationId = prayerKey.hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())
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
