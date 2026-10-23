package com.hathway.solatnadi

import androidx.compose.ui.window.ComposeUIViewController
import com.hathway.solatnadi.di.initKoin

private var isKoinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (!isKoinInitialized) {
        try {
            initKoin()
        } catch (_: Exception) {
            // Koin already started
        }
        isKoinInitialized = true
    }
    App()
}
