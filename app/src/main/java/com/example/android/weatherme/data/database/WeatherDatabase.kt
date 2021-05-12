package com.example.android.weatherme.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.weatherme.data.database.daos.CurrentDao
import com.example.android.weatherme.data.database.daos.DailyDao
import com.example.android.weatherme.data.database.daos.HourlyDao
import com.example.android.weatherme.data.database.entities.CurrentEntity
import com.example.android.weatherme.data.database.entities.DailyEntity
import com.example.android.weatherme.data.database.entities.HourlyEntity

@Database(
        entities = [CurrentEntity::class, HourlyEntity::class, DailyEntity::class],
        version = 1,
        exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun currentDao(): CurrentDao

    abstract fun hourlyDao(): HourlyDao

    abstract fun dailyDao(): DailyDao

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

/*@Volatile
private lateinit var INSTANCE: WeatherDatabase

fun getDatabase(context: Context): WeatherDatabase {
    synchronized(WeatherDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "database"
            ).build()
        }
    }
    return INSTANCE
}*/