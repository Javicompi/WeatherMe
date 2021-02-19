package com.example.android.weatherme.data

import android.location.Location
import androidx.lifecycle.LiveData
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApi
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val database: WeatherDatabase) : BaseRepository() {

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

    suspend fun searchCurrentByName(name: String): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherByName(location = name)
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherByLatLon(
                    latitude = location.latitude,
                    longitude = location.longitude
            )
        }
    }

    suspend fun searchCurrentByCityId(id: Long): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherById(id = id)
        }
    }

    suspend fun updateCurrents() {
        withContext(Dispatchers.IO) {
            val cityIds = database.currentWeatherDao().getCityIds()
            val currents = cityIds.map { id ->
                WeatherApi.retrofitService.getCurrentWeatherById(id = id)
            }
            for (current in currents) {
                if (current.id > 0) {
                    val entity = current.toEntity()
                    database.currentWeatherDao().insertCurrent(entity)
                }
            }
        }
    }
}