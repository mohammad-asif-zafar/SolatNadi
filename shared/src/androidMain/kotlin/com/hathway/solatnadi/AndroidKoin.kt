package com.hathway.solatnadi

import android.content.Context
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun initKoinAndroid(context: Context) {
    initKoin {
        androidContext(context)
        modules(
            module {
                single { DatabaseDriverFactory(get()) }
            }
        )
    }
}
