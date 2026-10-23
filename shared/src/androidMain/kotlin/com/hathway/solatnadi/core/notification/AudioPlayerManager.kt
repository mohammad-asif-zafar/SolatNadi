package com.hathway.solatnadi.core.notification

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, rawSoundRes: Int, onCompletion: (() -> Unit)? = null) {
        stopSound() // Stop any currently playing sound

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            val ringerMode = audioManager?.ringerMode ?: android.media.AudioManager.RINGER_MODE_NORMAL
            val currentVol = audioManager?.getStreamVolume(android.media.AudioManager.STREAM_ALARM) ?: 10
            val maxVol = audioManager?.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM) ?: 10

            if (ringerMode == android.media.AudioManager.RINGER_MODE_SILENT ||
                ringerMode == android.media.AudioManager.RINGER_MODE_VIBRATE ||
                currentVol == 0
            ) {
                onCompletion?.invoke()
                return
            }

            val volRatio = if (maxVol > 0) currentVol.toFloat() / maxVol.toFloat() else 1.0f

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            mediaPlayer = (MediaPlayer.create(context, rawSoundRes, audioAttributes, 0)
                ?: MediaPlayer.create(context, rawSoundRes))?.apply {
                setVolume(volRatio, volRatio)
                isLooping = false
                start()
                setOnCompletionListener {
                    it.release()
                    if (mediaPlayer == it) {
                        mediaPlayer = null
                    }
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion?.invoke()
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    fun stopSound() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            AlarmStateManager.clearActiveAlarm()
        }
    }
}
