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

        val isTestNotification = intent.getBooleanExtra("EXTRA_TEST_NOTIFICATION", false)

        var cityName = ""
        var soundKey = "default_adhan"
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
        val soundUri = Uri.parse("android.resource://${context.packageName}/$rawSoundRes")

        val channelId = "solatnadi_prayer_channel_$soundKey"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Prayer Time Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Islamic prayer times"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        if (soundKey != "silent") {
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                val currentVol = audioManager?.getStreamVolume(android.media.AudioManager.STREAM_ALARM) ?: 10
                val maxVol = audioManager?.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM) ?: 10
                val volRatio = if (maxVol > 0) currentVol.toFloat() / maxVol.toFloat() else 1.0f

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                val mediaPlayer = MediaPlayer.create(context, rawSoundRes, audioAttributes, 0)
                    ?: MediaPlayer.create(context, rawSoundRes)

                mediaPlayer?.apply {
                    setVolume(volRatio, volRatio)
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
        }

        val titleText = if (cityName.isNotEmpty()) "SolatNadi — $prayerDisplayName ($cityName)" else "SolatNadi — $prayerDisplayName"
        val contentText = if (cityName.isNotEmpty()) "It is time for $prayerDisplayName prayer in $cityName." else "It is time for $prayerDisplayName prayer."

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setAutoCancel(true)

        if (pendingContentIntent != null) {
            notificationBuilder.setContentIntent(pendingContentIntent)
        }

        val notificationId = prayerKey.hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun getRawResourceForSoundKey(soundKey: String): Int {
        return when (soundKey) {
            "allahu_akbar" -> R.raw.allahu_akbar
            "makkah_adhan", "default_adhan" -> R.raw.lebanon_adhan
            "oman_adhan" -> R.raw.oman_adhan
            "qatar_adhan" -> R.raw.qatar_adhan
            "egypt_fajr_adhan" -> R.raw.egypt_fajr_adhan
            "lebanon_adhan" -> R.raw.lebanon_adhan
            "abdel_mobdi_adhan" -> R.raw.ressources_audio_allahuakbar
            "alarm_azan" -> R.raw.alarm_azan
            else -> R.raw.alarm_azan
        }
    }
}
