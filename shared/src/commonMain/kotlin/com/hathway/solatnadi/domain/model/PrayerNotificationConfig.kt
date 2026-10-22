package com.hathway.solatnadi.domain.model

data class AdhanTone(
    val key: String,
    val title: String,
    val subtitle: String,
    val category: ToneCategory = ToneCategory.ADHAN
)

enum class ToneCategory {
    ADHAN, ALARMS, DEVICE
}

object AdhanTones {
    val DefaultMakkah = AdhanTone("default_adhan", "Adhan Makkah", "Mecca", ToneCategory.ADHAN)
    val Oman = AdhanTone("oman_adhan", "Adhan Muscat", "Oman", ToneCategory.ADHAN)
    val Qatar = AdhanTone("qatar_adhan", "Ali Hassan", "Qatar", ToneCategory.ADHAN)
    val EgyptFajr = AdhanTone("egypt_fajr_adhan", "Abdulbasit Abdusamad", "Fajr Egypt", ToneCategory.ADHAN)
    val Lebanon = AdhanTone("lebanon_adhan", "Abd Alrazaq Saleh", "Lebanon", ToneCategory.ADHAN)
    val Takbeer = AdhanTone("allahu_akbar", "Allahu Akbar", "Short Takbeer", ToneCategory.ADHAN)
    val ClassicAzan = AdhanTone("alarm_azan", "Classic Azan", "Full Adhan", ToneCategory.ADHAN)

    val SystemDefault = AdhanTone("system_default", "System Default Tone", "Device Notification", ToneCategory.ALARMS)
    val GentleChime = AdhanTone("gentle_chime", "Gentle Chime", "Soft Reminder", ToneCategory.ALARMS)
    val Silent = AdhanTone("silent", "Silent / Mute", "Vibration only or silent", ToneCategory.DEVICE)

    val allTones = listOf(
        DefaultMakkah, Oman, Qatar, EgyptFajr, Lebanon, Takbeer, ClassicAzan, SystemDefault, GentleChime, Silent
    )

    fun findByKey(key: String): AdhanTone {
        return allTones.find { it.key == key } ?: DefaultMakkah
    }
}



data class PrayerNotificationConfig(
    val prayerName: PrayerName,
    val enabled: Boolean = true,
    val reminderMinutes: Int = 10,
    val soundKey: String = "default_adhan",
    val vibrationEnabled: Boolean = false,
    val repeatReminderEnabled: Boolean = false
) {
    val soundTone: AdhanTone
        get() = AdhanTones.findByKey(soundKey)

    val reminderText: String
        get() = when (reminderMinutes) {
            0 -> "At prayer time"
            else -> "$reminderMinutes minutes before"
        }
}
