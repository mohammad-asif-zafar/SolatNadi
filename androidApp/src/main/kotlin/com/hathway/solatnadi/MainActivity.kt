package com.hathway.solatnadi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.hathway.solatnadi.core.notification.AlarmStateManager
import com.hathway.solatnadi.data.mapper.formatTo12Hour
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        checkNotificationPermission()
        handleAlarmIntent(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAlarmIntent(intent)
    }

    private fun handleAlarmIntent(intent: Intent?) {
        val prayerKey = intent?.getStringExtra("EXTRA_PRAYER_KEY")
        val prayerDisplayName = intent?.getStringExtra("EXTRA_PRAYER_NAME") ?: "Prayer"
        if (prayerKey != null) {
            val prayerNameEnum = PrayerName.entries.firstOrNull {
                it.name.equals(prayerKey, ignoreCase = true) || it.displayName.equals(prayerDisplayName, ignoreCase = true)
            } ?: PrayerName.FAJR
            val cal = Calendar.getInstance()
            val formattedTime = formatTo12Hour(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            AlarmStateManager.triggerAlarm(
                ActiveAlarmData(
                    prayerName = prayerNameEnum,
                    timeFormatted = formattedTime
                )
            )
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
