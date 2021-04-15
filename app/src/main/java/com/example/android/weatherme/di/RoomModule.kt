package com.example.android.weatherme.di

import android.content.Context
import androidx.room.Room
import com.example.android.weatherme.data.database.CurrentDao
import com.example.android.weatherme.data.database.PerHourDao
import com.example.android.weatherme.data.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "Weather.db"
        ).build()
    }

    @Provides
    fun provideCurrentWeatherDao(database: WeatherDatabase): CurrentDao {
        return database.currentDao()
    }

    @Provides
    fun providePerHourDao(database: WeatherDatabase): PerHourDao {
        return database.perHourDao()
    }
}