package com.hathway.solatnadi.core.notification

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
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

    fun playSound(soundKey: String, onCompletion: (() -> Unit)? = null) {
        stopSound()

        val fileName = getFileNameForSoundKey(soundKey)
        if (fileName == "silent") {
            onCompletion?.invoke()
            return
        }

        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, error = null)
            audioSession.setActive(true, error = null)

            val soundNameWithoutExtension = fileName.substringBeforeLast(".")
            val bundlePath = NSBundle.mainBundle.pathForResource(soundNameWithoutExtension, ofType = "mp3")
                ?: NSBundle.mainBundle.pathForResource(soundNameWithoutExtension, ofType = "caf")

            if (bundlePath != null) {
                val url = NSURL.fileURLWithPath(bundlePath)
                startPlayerWithUrl(url, onCompletion)
            } else {
                // Fallback to Compose Resources via Coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val bytes = Res.readBytes("files/$fileName")
                        val nsData = bytes.usePinned { pinned ->
                            NSData.dataWithBytes(pinned.addressOf(0), length = bytes.size.toULong())
                        }
                        startPlayerWithData(nsData, onCompletion)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onCompletion?.invoke()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion?.invoke()
        }
    }

    private fun startPlayerWithUrl(url: NSURL, onCompletion: (() -> Unit)?) {
        try {
            val player = AVAudioPlayer(contentsOfURL = url, error = null)
            if (player != null) {
                if (onCompletion != null) {
                    val delegate = AudioDelegate {
                        stopSound()
                        onCompletion()
                    }
                    activeDelegate = delegate
                    player.delegate = delegate
                }
                audioPlayer = player
                player.play()
            } else {
                onCompletion?.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion?.invoke()
        }
    }

    private fun startPlayerWithData(data: NSData, onCompletion: (() -> Unit)?) {
        try {
            val player = AVAudioPlayer(data = data, error = null)
            if (player != null) {
                if (onCompletion != null) {
                    val delegate = AudioDelegate {
                        stopSound()
                        onCompletion()
                    }
                    activeDelegate = delegate
                    player.delegate = delegate
                }
                audioPlayer = player
                player.play()
            } else {
                onCompletion?.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onCompletion?.invoke()
        }
    }

    fun stopSound() {
        try {
            audioPlayer?.let {
                if (it.isPlaying()) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioPlayer = null
            activeDelegate = null
            AlarmStateManager.clearActiveAlarm()
        }
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

    private class AudioDelegate(private val onCompletion: () -> Unit) : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            onCompletion()
        }
    }
}
