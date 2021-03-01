package com.example.android.weatherme.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.perhour.HourlyEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourEntity

@Database(entities = [
    CurrentEntity::class, HourlyEntity::class, PerHourEntity::class
                     ], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao

    abstract fun perHourWeatherDao(): PerHourDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        "Weather.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}