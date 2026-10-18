package com.hathway.solatnadi.di

import com.hathway.solatnadi.core.notification.NotificationScheduler
import org.koin.dsl.module

actual val platformNotificationModule = module {
    single { NotificationScheduler(get()) }
}
