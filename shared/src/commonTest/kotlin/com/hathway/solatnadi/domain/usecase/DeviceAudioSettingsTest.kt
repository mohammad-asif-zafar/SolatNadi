package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.AdhanTones
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceAudioSettingsTest {

    // Helper functions mirroring system audio decision logic in PrayerAlarmReceiver & NotificationScheduler
    private fun computeVolumeRatio(currentVol: Int, maxVol: Int): Float {
        return if (maxVol > 0) currentVol.toFloat() / maxVol.toFloat() else 1.0f
    }

    private fun shouldPlayAudio(ringerMode: Int, currentVol: Int, soundKey: String): Boolean {
        val ringerModeSilent = 0
        val ringerModeVibrate = 1

        if (soundKey == "silent") return false
        if (ringerMode == ringerModeSilent || ringerMode == ringerModeVibrate || currentVol == 0) {
            return false
        }
        return true
    }

    @Test
    fun testVolumeRatioCalculationForNormalVolume() {
        val ratio = computeVolumeRatio(currentVol = 8, maxVol = 10)
        assertEquals(0.8f, ratio, 0.001f)
    }

    @Test
    fun testVolumeRatioCalculationForReducedVolume() {
        val ratio = computeVolumeRatio(currentVol = 2, maxVol = 10)
        assertEquals(0.2f, ratio, 0.001f)
    }

    @Test
    fun testVolumeRatioCalculationForZeroVolume() {
        val ratio = computeVolumeRatio(currentVol = 0, maxVol = 10)
        assertEquals(0.0f, ratio, 0.001f)
    }

    @Test
    fun testSilentRingerModeMutesAudioPlayback() {
        val canPlay = shouldPlayAudio(ringerMode = 0, currentVol = 8, soundKey = "default_adhan")
        assertFalse(canPlay, "Audio must be muted in SILENT mode")
    }

    @Test
    fun testVibrateRingerModeMutesAudioPlayback() {
        val canPlay = shouldPlayAudio(ringerMode = 1, currentVol = 8, soundKey = "default_adhan")
        assertFalse(canPlay, "Audio must be muted in VIBRATE mode")
    }

    @Test
    fun testNormalRingerModeWithVolumePlaysAudio() {
        val canPlay = shouldPlayAudio(ringerMode = 2, currentVol = 5, soundKey = "default_adhan")
        assertTrue(canPlay, "Audio should play in NORMAL mode with active stream volume")
    }

    @Test
    fun testSilentSoundKeyMutesAudioPlayback() {
        val canPlay = shouldPlayAudio(ringerMode = 2, currentVol = 8, soundKey = "silent")
        assertFalse(canPlay, "Audio must be muted when soundKey is set to 'silent'")
    }

    @Test
    fun testAllAdhanTonesKeysExistAndResolve() {
        AdhanTones.allTones.forEach { tone ->
            val resolved = AdhanTones.findByKey(tone.key)
            assertEquals(tone.key, resolved.key)
        }
    }
}
