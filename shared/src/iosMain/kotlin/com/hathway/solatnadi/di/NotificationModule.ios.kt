package com.hathway.solatnadi.di

import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.core.notification.NotificationScheduler
import org.koin.dsl.module

actual val platformNotificationModule = module {
    single { DatabaseDriverFactory() }
    single { NotificationScheduler() }
}
