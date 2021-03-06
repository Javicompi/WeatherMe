package com.example.android.weatherme.di

import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.daos.CurrentDao
import com.example.android.weatherme.data.database.daos.DailyDao
import com.example.android.weatherme.data.database.daos.HourlyDao
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.utils.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        @IoDispatcher
        dbDispatcher: CoroutineDispatcher,
        currentDao: CurrentDao,
        hourlyDao: HourlyDao,
        dailyDao: DailyDao,
        weatherApiService: WeatherApiService,
        preferencesHelper: PreferencesHelper
    ): Repository {
        return Repository(
                dbDispatcher,
                currentDao,
                hourlyDao,
                dailyDao,
                weatherApiService,
                preferencesHelper
        )
    }
}