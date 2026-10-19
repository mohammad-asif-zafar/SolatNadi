package com.hathway.solatnadi.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.core.database.createDatabase
import com.hathway.solatnadi.domain.model.PrayerName
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            try {
                val database = createDatabase(DatabaseDriverFactory(context))
                val location = database.solatNadiDatabaseQueries
                    .selectSelectedLocation()
                    .executeAsOneOrNull() ?: return

                val notifPrefs = database.solatNadiDatabaseQueries
                    .getNotificationPreferences()
                    .executeAsList()
                    .associate { it.prayerName to (it.enabled == 1L) }

                val tz = try {
                    TimeZone.of(location.timezone)
                } catch (_: Exception) {
                    TimeZone.currentSystemDefault()
                }

                @OptIn(kotlin.time.ExperimentalTime::class)
                val todayDate = Instant.fromEpochMilliseconds(kotlin.time.Clock.System.now().toEpochMilliseconds())
                    .toLocalDateTime(tz).date

                val prayerTimes = database.solatNadiDatabaseQueries
                    .getPrayerTimeForDate(location.id, "${todayDate.year}-${todayDate.monthNumber.toString().padStart(2, '0')}-${todayDate.dayOfMonth.toString().padStart(2, '0')}")
                    .executeAsOneOrNull() ?: return

                val scheduler = NotificationScheduler(context)

                fun scheduleIfEnabled(name: PrayerName, timeRaw: String) {
                    val enabled = notifPrefs[name.name] ?: true
                    if (enabled) {
                        val cleanTime = timeRaw.split(" ").firstOrNull() ?: timeRaw
                        val parts = cleanTime.split(":")
                        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
                        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        @Suppress("DEPRECATION")
                        val dateTime = LocalDateTime(todayDate.year, todayDate.monthNumber, todayDate.dayOfMonth, hour, minute, 0)
                        scheduler.schedulePrayerNotification(name, dateTime)
                    } else {
                        scheduler.cancelPrayerNotification(name)
                    }
                }

                scheduleIfEnabled(PrayerName.FAJR, prayerTimes.fajr)
                scheduleIfEnabled(PrayerName.DHUHR, prayerTimes.dhuhr)
                scheduleIfEnabled(PrayerName.ASR, prayerTimes.asr)
                scheduleIfEnabled(PrayerName.MAGHRIB, prayerTimes.maghrib)
                scheduleIfEnabled(PrayerName.ISHA, prayerTimes.isha)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
