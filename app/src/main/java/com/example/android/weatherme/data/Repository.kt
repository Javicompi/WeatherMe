package com.example.android.weatherme.data

import android.app.Application
import androidx.room.Room
import com.example.android.weatherme.data.database.Result
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val database: WeatherDatabase) {

    suspend fun saveCurrent(current: CurrentEntity) {
        withContext(Dispatchers.IO) {
            database.currentWeatherDao().insertCurrent(current)
        }
    }

    suspend fun getCurrents(): Result<List<CurrentEntity>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(database.currentWeatherDao().getCurrents())
        } catch (exception: Exception) {
            Result.Error(exception.localizedMessage)
        }
    }

    suspend fun getCurrentByName(name: String): Result<CurrentEntity> = withContext(Dispatchers.IO) {
        try {
            val current = database.currentWeatherDao().getCurrentByName(name)
            if (current.isSuccess()) {
                return@withContext Result.Success(current)
            } else {
                return@withContext Result.Error("Current not found")
            }
        } catch (exception: Exception) {
            return@withContext Result.Error(exception.localizedMessage)
        }
    }

    suspend fun getCurrentByKey(key: Long): Result<CurrentEntity> = withContext(Dispatchers.IO) {
        try {
            val current = database.currentWeatherDao().getCurrentByKey(key)
            if (current.isSuccess()) {
                return@withContext Result.Success(current)
            } else {
                return@withContext Result.Error("Reminder not found")
            }
        } catch (exception: Exception) {
            return@withContext Result.Error(exception.localizedMessage)
        }
    }

    suspend fun deleteCurrents() = withContext(Dispatchers.IO) {
        database.currentWeatherDao().deleteCurrents()
    }

    companion object {

        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(app: Application): Repository {

            return INSTANCE ?: synchronized(this) {

                val database = Room.databaseBuilder(
                    app,
                    WeatherDatabase::class.java,
                    "Weather.db"
                ).build()
                Repository(database).also {
                    INSTANCE = it
                }
            }
        }
    }
}