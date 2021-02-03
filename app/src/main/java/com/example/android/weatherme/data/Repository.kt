package com.example.android.weatherme.data

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.android.weatherme.data.database.Result
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.WeatherApi
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.preferences.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val database: WeatherDatabase, private val dataStore: Preferences) {

    private val TAG = "Repository"

    suspend fun saveCurrent(current: CurrentEntity): Long {
        var id: Long
        withContext(Dispatchers.IO) {
            id = database.currentWeatherDao().insertCurrent(current)
            dataStore.storeCurrentSelected(id)
        }
        return id
    }

    suspend fun getCurrents(): Result<List<CurrentEntity>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(database.currentWeatherDao().getCurrents())
        } catch (exception: Exception) {
            Result.Error(exception.localizedMessage)
        }
    }

    suspend fun getCurrentByKey(key: Long = 0L): Result<CurrentEntity> = withContext(Dispatchers.IO) {
        try {
            val current: CurrentEntity
            if (key == 0L) {
                val savedKey = dataStore.getCurrentSelected()
                if (savedKey == 0L) {
                    current = database.currentWeatherDao().getFirstCurrent()
                    if (current.isSuccess()) {
                        dataStore.storeCurrentSelected(current.key)
                        return@withContext Result.Success(current)
                    } else {
                        return@withContext Result.Error("Not found")
                    }
                } else {
                    current = database.currentWeatherDao().getCurrentByKey(savedKey)
                    return@withContext Result.Success(current)
                }
            } else {
                current = database.currentWeatherDao().getCurrentByKey(key)
                if (current.isSuccess()) {
                    dataStore.storeCurrentSelected(current.key)
                    return@withContext Result.Success(current)
                } else {
                    return@withContext Result.Error("Not found")
                }
            }
        } catch (exception: Exception) {
            return@withContext Result.Error(exception.localizedMessage)
        }
    }

    suspend fun deleteCurrent(key: Long) {
        database.currentWeatherDao().deleteCurrent(key)
        dataStore.storeCurrentSelected(0)
    }

    suspend fun deleteCurrents() = withContext(Dispatchers.IO) {
        database.currentWeatherDao().deleteCurrents()
        dataStore.storeCurrentSelected(0)
    }

    suspend fun searchCurrentByName(name: String): Current {
        return WeatherApi.retrofitService.getCurrentWeatherByName(name)
    }

    suspend fun searchCurrentByLatLon(lat: Double, lon: Double):Current {
        return WeatherApi.retrofitService.getCurrentWeatherByLatLon(lat, lon)
    }

    suspend fun updateCurrents() = withContext(Dispatchers.IO) {
        val currents = database.currentWeatherDao().getCurrents()
        if (currents.isNotEmpty()) {
            for (current in currents) {
                Log.d(TAG, "updating ${current.cityName}")
                val updated = WeatherApi.retrofitService.getCurrentWeatherByName(current.cityName)
                updated.let {
                    val entity = updated.toEntity()
                    entity.key = current.key
                    database.currentWeatherDao().insertCurrent(entity)
                }
            }
        }
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
                val dataStore = Preferences(app)
                Repository(database, dataStore).also {
                    INSTANCE = it
                }
            }
        }
    }
}