package com.hathway.solatnadi.di

import com.hathway.solatnadi.core.database.DatabaseDriverFactory
import com.hathway.solatnadi.core.database.createDatabase
import com.hathway.solatnadi.data.remote.PrayerTimesApi
import com.hathway.solatnadi.data.remote.createDefaultHttpClient
import com.hathway.solatnadi.data.repository.PreferencesRepositoryImpl
import com.hathway.solatnadi.data.repository.PrayerTimesRepositoryImpl
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.repository.PrayerTimesRepository
import com.hathway.solatnadi.domain.usecase.GetAppPreferencesUseCase
import com.hathway.solatnadi.domain.usecase.GetNextPrayerUseCase
import com.hathway.solatnadi.domain.usecase.ObservePrayerTimesUseCase
import com.hathway.solatnadi.domain.usecase.RefreshPrayerTimesUseCase
import com.hathway.solatnadi.domain.usecase.ToggleNotificationUseCase
import com.hathway.solatnadi.domain.usecase.UpdateAppPreferencesUseCase
import com.hathway.solatnadi.presentation.home.HomeViewModel
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single { createDefaultHttpClient() }
    single { PrayerTimesApi(get()) }
}

val databaseModule = module {
    single { createDatabase(get<DatabaseDriverFactory>()) }
}

val repositoryModule = module {
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<PrayerTimesRepository> { PrayerTimesRepositoryImpl(get(), get(), get()) }
}

val useCaseModule = module {
    factory { ObservePrayerTimesUseCase(get()) }
    factory { RefreshPrayerTimesUseCase(get()) }
    factory { GetNextPrayerUseCase() }
    factory { ToggleNotificationUseCase(get()) }
    factory { GetAppPreferencesUseCase(get()) }
    factory { UpdateAppPreferencesUseCase(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}

val appModule = module {
    includes(networkModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModule)
    }
}
