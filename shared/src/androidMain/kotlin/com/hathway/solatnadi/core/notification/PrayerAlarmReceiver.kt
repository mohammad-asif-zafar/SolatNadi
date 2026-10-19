package com.hathway.solatnadi.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.core.database.createDatabase
import com.hathway.solatnadi.shared.R

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SolatNadi:AzanAlarmWakeLock")
        wakeLock?.acquire(30000)

        val prayerKey = intent.getStringExtra("EXTRA_PRAYER_KEY") ?: intent.getStringExtra("EXTRA_PRAYER_NAME")?.uppercase() ?: "FAJR"
        val prayerDisplayName = intent.getStringExtra("EXTRA_PRAYER_NAME") ?: "Prayer"

        var cityName = ""
        try {
            val database = createDatabase(DatabaseDriverFactory(context))
            val pref = database.solatNadiDatabaseQueries
                .getNotificationPreferenceForPrayer(prayerKey)
                .executeAsOneOrNull()

            if (pref != null && pref.enabled == 0L) {
                if (wakeLock?.isHeld == true) {
                    try { wakeLock.release() } catch (_: Exception) {}
                }
                return
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

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_azan}")

        val channelId = "solatnadi_prayer_channel_v3"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Prayer Time Azan Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Islamic prayer times with Azan audio"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_azan, audioAttributes, 0)
                ?: MediaPlayer.create(context, R.raw.alarm_azan)

            mediaPlayer?.apply {
                isLooping = false
                start()
                setOnCompletionListener {
                    it.release()
                    if (wakeLock?.isHeld == true) {
                        try { wakeLock.release() } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (wakeLock?.isHeld == true) {
                try { wakeLock.release() } catch (_: Exception) {}
            }
        }

        val titleText = if (cityName.isNotEmpty()) "SolatNadi — $prayerDisplayName ($cityName)" else "SolatNadi — $prayerDisplayName"
        val contentText = if (cityName.isNotEmpty()) "It is time for $prayerDisplayName prayer in $cityName." else "It is time for $prayerDisplayName prayer."

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        val notificationId = prayerKey.hashCode()
        notificationManager.notify(notificationId, notification)
    }
}
