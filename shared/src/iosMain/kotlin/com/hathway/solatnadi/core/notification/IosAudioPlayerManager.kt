package com.hathway.solatnadi.core.notification

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategorySoloAmbient
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.darwin.NSObject
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.addressOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import solatnadi.shared.generated.resources.Res

@OptIn(ExperimentalForeignApi::class)
object IosAudioPlayerManager {
    private var audioPlayer: AVAudioPlayer? = null
    private var activeDelegate: AudioDelegate? = null
    val coordinator = AudioLifecycleCoordinator()

    fun playSound(soundKey: String, onCompletion: (() -> Unit)? = null) {
        val session = coordinator.newSession()
        releasePlayer()

        val fileName = getFileNameForSoundKey(soundKey)
        if (fileName == "silent") {
            if (!coordinator.isStaleSession(session)) {
                coordinator.handlePlaybackFailure(session)
                onCompletion?.invoke()
            }
            return
        }

        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategorySoloAmbient, error = null)
            audioSession.setActive(true, error = null)

            val soundNameWithoutExtension = fileName.substringBeforeLast(".")
            val bundlePath = NSBundle.mainBundle.pathForResource(soundNameWithoutExtension, ofType = "mp3")
                ?: NSBundle.mainBundle.pathForResource(soundNameWithoutExtension, ofType = "caf")

            if (bundlePath != null) {
                if (coordinator.isStaleSession(session)) return
                val url = NSURL.fileURLWithPath(bundlePath)
                startPlayerWithUrl(url, session, onCompletion)
            } else {
                // Fallback to Compose Resources via Coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        if (coordinator.isStaleSession(session)) return@launch
                        val bytes = Res.readBytes("files/$fileName")
                        if (coordinator.isStaleSession(session)) return@launch
                        val nsData = bytes.usePinned { pinned ->
                            NSData.dataWithBytes(pinned.addressOf(0), length = bytes.size.toULong())
                        }
                        if (coordinator.isStaleSession(session)) return@launch
                        startPlayerWithData(nsData, session, onCompletion)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (!coordinator.isStaleSession(session)) {
                            coordinator.handlePlaybackFailure(session)
                            onCompletion?.invoke()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (!coordinator.isStaleSession(session)) {
                coordinator.handlePlaybackFailure(session)
                onCompletion?.invoke()
            }
        }
    }

    private fun startPlayerWithUrl(url: NSURL, session: Long, onCompletion: (() -> Unit)?) {
        try {
            if (coordinator.isStaleSession(session)) return
            val player = AVAudioPlayer(contentsOfURL = url, error = null)
            if (player != null) {
                val delegate = AudioDelegate(session, player, { coordinator.sessionId }) { targetPlayer, targetDelegate, successful ->
                    // Mandatory callback validation: accept ONLY when session is current AND player matches AND delegate matches
                    if (!coordinator.isStaleSession(session) && audioPlayer === targetPlayer && activeDelegate === targetDelegate) {
                        if (successful) {
                            coordinator.handleNaturalCompletion(session)
                        } else {
                            coordinator.handlePlaybackFailure(session)
                        }
                        releaseSpecificPlayer(targetPlayer, targetDelegate)
                        onCompletion?.invoke()
                    }
                }
                activeDelegate = delegate
                player.delegate = delegate
                audioPlayer = player
                player.play()
            } else {
                if (!coordinator.isStaleSession(session)) {
                    coordinator.handlePlaybackFailure(session)
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (!coordinator.isStaleSession(session)) {
                coordinator.handlePlaybackFailure(session)
                onCompletion?.invoke()
            }
        }
    }

    private fun startPlayerWithData(data: NSData, session: Long, onCompletion: (() -> Unit)?) {
        try {
            if (coordinator.isStaleSession(session)) return
            val player = AVAudioPlayer(data = data, error = null)
            if (player != null) {
                val delegate = AudioDelegate(session, player, { coordinator.sessionId }) { targetPlayer, targetDelegate, successful ->
                    // Mandatory callback validation: accept ONLY when session is current AND player matches AND delegate matches
                    if (!coordinator.isStaleSession(session) && audioPlayer === targetPlayer && activeDelegate === targetDelegate) {
                        if (successful) {
                            coordinator.handleNaturalCompletion(session)
                        } else {
                            coordinator.handlePlaybackFailure(session)
                        }
                        releaseSpecificPlayer(targetPlayer, targetDelegate)
                        onCompletion?.invoke()
                    }
                }
                activeDelegate = delegate
                player.delegate = delegate
                audioPlayer = player
                player.play()
            } else {
                if (!coordinator.isStaleSession(session)) {
                    coordinator.handlePlaybackFailure(session)
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (!coordinator.isStaleSession(session)) {
                coordinator.handlePlaybackFailure(session)
                onCompletion?.invoke()
            }
        }
    }

    /**
     * Instance-safe player release: releases player and delegate ONLY if they still refer to target instance.
     */
    private fun releaseSpecificPlayer(playerToRelease: AVAudioPlayer, delegateToRelease: AudioDelegate?) {
        try {
            if (audioPlayer === playerToRelease) {
                audioPlayer?.delegate = null
                if (audioPlayer?.isPlaying() == true) {
                    audioPlayer?.stop()
                }
                audioPlayer = null
            } else {
                playerToRelease.delegate = null
                if (playerToRelease.isPlaying()) {
                    playerToRelease.stop()
                }
            }
            if (activeDelegate === delegateToRelease) {
                activeDelegate = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Releases audio player resources instance-safely without unconditional global reference erasure.
     */
    fun releasePlayer() {
        try {
            audioPlayer?.let { player ->
                releaseSpecificPlayer(player, activeDelegate)
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

    fun isPlaying(): Boolean {
        return try {
            audioPlayer?.isPlaying() == true
        } catch (e: Exception) {
            false
        }
    }

    private fun getFileNameForSoundKey(soundKey: String): String {
        return when (soundKey) {
            "allahu_akbar" -> "allahu_akbar.mp3"
            "makkah_adhan", "default_adhan" -> "adhan_mecca.mp3"
            "oman_adhan" -> "oman_adhan.mp3"
            "qatar_adhan" -> "qatar_adhan.mp3"
            "egypt_fajr_adhan" -> "egypt_fajr_adhan.mp3"
            "lebanon_adhan" -> "lebanon_adhan.mp3"
            "alarm_azan" -> "alarm_azan.mp3"
            "silent" -> "silent"
            else -> "adhan_mecca.mp3"
        }
    }

    private class AudioDelegate(
        private val session: Long,
        private val playerInstance: AVAudioPlayer,
        private val getCurrentSessionId: () -> Long,
        private val onCompletion: (AVAudioPlayer, AudioDelegate, Boolean) -> Unit
    ) : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            if (player == playerInstance && session == getCurrentSessionId()) {
                onCompletion(player, this, successfully)
            }
        }
    }
}
