package com.example.android.weatherme.data

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.WeatherApi
import com.example.android.weatherme.data.network.models.current.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val database: WeatherDatabase) {

    private val TAG = "Repository"

    suspend fun saveCurrent(current: CurrentEntity): Long {
        var id: Long
        withContext(Dispatchers.IO) {
            id = database.currentWeatherDao().insertCurrent(current)
        }
        return id
    }

    fun getCurrents(): LiveData<List<CurrentEntity>> {
        return database.currentWeatherDao().getCurrents()
    }

    fun getCurrentByKey(key: Long): LiveData<CurrentEntity> {
        return database.currentWeatherDao().getCurrentByKey(key)
    }

    fun getCurrentByKName(name: String): LiveData<CurrentEntity> {
        return database.currentWeatherDao().getCurrentByName(name)
    }

    suspend fun deleteCurrent(key: Long) = withContext(Dispatchers.IO) {
        database.currentWeatherDao().deleteCurrent(key)
    }

    suspend fun deleteCurrents() = withContext(Dispatchers.IO) {
        database.currentWeatherDao().deleteCurrents()
    }

    suspend fun searchCurrentByName(name: String): CurrentEntity {
        val result = WeatherApi.retrofitService.getCurrentWeatherByName(name)
        return result.toEntity()
    }

    suspend fun searchCurrentByLatLon(location: Location): CurrentEntity {
        val result = WeatherApi.retrofitService.getCurrentWeatherByLatLon(location.latitude, location.longitude)
        return result.toEntity()
    }

    suspend fun updateCurrents() {
        val currents = database.currentWeatherDao().getCurrents()
        if (currents.value != null && currents.value?.isNotEmpty() == true) {
            for (current in currents.value!!) {
                Log.d(TAG, "updating ${current.cityName}")
                val updated = WeatherApi.retrofitService.getCurrentWeatherByName(current.cityName)
                updated.let {
                    val newEntity = updated.toEntity()
                    newEntity.key = current.key
                    database.currentWeatherDao().insertCurrent(newEntity)
                }
            }
        }
    }

    suspend fun currentExists(cityName: String): Boolean = withContext(Dispatchers.IO) {
        val value = database.currentWeatherDao().count(cityName)
        return@withContext value > 0
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