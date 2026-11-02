package com.hathway.solatnadi.core.notification

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import java.util.concurrent.atomic.AtomicBoolean

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    val coordinator = AudioLifecycleCoordinator()

    fun playSound(context: Context, rawSoundRes: Int, onCompletion: (() -> Unit)? = null) {
        val session = coordinator.newSession()
        releasePlayer() // Release player resources without affecting active alarm state

        val hasCompleted = AtomicBoolean(false)

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            val ringerMode = audioManager?.ringerMode ?: android.media.AudioManager.RINGER_MODE_NORMAL
            val currentVol = audioManager?.getStreamVolume(android.media.AudioManager.STREAM_ALARM) ?: 10
            val maxVol = audioManager?.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM) ?: 10

            val isNormalMode = ringerMode == android.media.AudioManager.RINGER_MODE_NORMAL

            if (!isNormalMode || currentVol == 0) {
                if (hasCompleted.compareAndSet(false, true)) {
                    if (!coordinator.isStaleSession(session)) {
                        coordinator.handlePlaybackFailure(session)
                        onCompletion?.invoke()
                    }
                }
                return
            }

            val volRatio = if (maxVol > 0) currentVol.toFloat() / maxVol.toFloat() else 1.0f

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val player = MediaPlayer.create(context, rawSoundRes, audioAttributes, 0)
                ?: MediaPlayer.create(context, rawSoundRes)

            if (player == null) {
                if (hasCompleted.compareAndSet(false, true)) {
                    if (!coordinator.isStaleSession(session)) {
                        coordinator.handlePlaybackFailure(session)
                        onCompletion?.invoke()
                    }
                }
                return
            }

            val playerInstance = player
            try {
                playerInstance.setVolume(volRatio, volRatio)
                playerInstance.isLooping = false
                playerInstance.setOnCompletionListener { completedPlayer ->
                    if (!coordinator.isStaleSession(session) && mediaPlayer === completedPlayer && completedPlayer === playerInstance) {
                        if (hasCompleted.compareAndSet(false, true)) {
                            coordinator.handleNaturalCompletion(session)
                            releaseSpecificPlayer(completedPlayer)
                            onCompletion?.invoke()
                        }
                    }
                }
                mediaPlayer = playerInstance
                playerInstance.start()
            } catch (e: Exception) {
                e.printStackTrace()
                if (mediaPlayer === playerInstance) {
                    mediaPlayer = null
                }
                releaseSpecificPlayer(playerInstance)
                if (hasCompleted.compareAndSet(false, true)) {
                    if (!coordinator.isStaleSession(session)) {
                        coordinator.handlePlaybackFailure(session)
                        onCompletion?.invoke()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (hasCompleted.compareAndSet(false, true)) {
                if (!coordinator.isStaleSession(session)) {
                    coordinator.handlePlaybackFailure(session)
                    onCompletion?.invoke()
                }
            }
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Instance-safe player release: releases player ONLY if it refers to target instance,
     * ensuring failures during listener removal or stop do not prevent release.
     */
    fun releaseSpecificPlayer(playerToRelease: MediaPlayer) {
        try {
            try {
                playerToRelease.setOnCompletionListener(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (playerToRelease.isPlaying) {
                    playerToRelease.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                playerToRelease.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (mediaPlayer === playerToRelease) {
                mediaPlayer = null
            }
        }
    }

    /**
     * Releases audio player resources instance-safely without unconditional global reference erasure.
     */
    fun releasePlayer() {
        try {
            mediaPlayer?.let { player ->
                releaseSpecificPlayer(player)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stops current audio playback by invalidating the session atomically and releasing resources.
     */
    fun stopPlayback() {
        coordinator.stopPlayback()
        releasePlayer()
    }

    /**
     * Explicitly clears the active alarm state and invalidates the session.
     */
    fun dismissAlarm() {
        coordinator.explicitDismissal()
    }
}
