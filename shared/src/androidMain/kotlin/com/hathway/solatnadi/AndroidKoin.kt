package com.hathway.solatnadi

import android.content.Context
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.di.initKoin
import org.koin.dsl.module

fun initKoinAndroid(context: Context) {
    val appContext = context.applicationContext
    initKoin {
        modules(
            module {
                single<Context> { appContext }
                single { DatabaseDriverFactory(get()) }
            }
        )
    }
}
