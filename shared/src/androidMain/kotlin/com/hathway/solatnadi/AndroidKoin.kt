package com.hathway.solatnadi

import android.content.Context
import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.di.initKoin
import org.koin.dsl.module

fun initKoinAndroid(context: Context) {
    initKoin {
        modules(
            module {
                single { DatabaseDriverFactory(context) }
            }
        )
    }
}
