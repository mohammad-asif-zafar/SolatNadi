package com.hathway.solatnadi

import android.app.Application

class SolatNadiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
