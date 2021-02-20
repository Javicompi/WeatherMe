package com.example.android.weatherme.di

import android.content.Context
import androidx.room.Room
import com.example.android.weatherme.data.database.CurrentWeatherDao
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
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "Weather.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCurrentWeatherDao(database: WeatherDatabase): CurrentWeatherDao {
        return database.currentWeatherDao()
    }
}