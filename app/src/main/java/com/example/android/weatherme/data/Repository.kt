package com.example.android.weatherme.data

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.data.database.CurrentWeatherDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.utils.PreferencesHelper
import com.example.android.weatherme.utils.shouldUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherApiService: WeatherApiService,
    private val preferencesHelper: PreferencesHelper
) : BaseRepository(){

    suspend fun saveCurrent(current: CurrentEntity): Long {
        Log.d("Repository", "save current")
        var id: Long
        withContext(Dispatchers.IO) {
            id = currentWeatherDao.insertCurrent(current)
        }
        return id
    }

    fun getCurrents(): LiveData<List<CurrentEntity>> {
        return currentWeatherDao.getCurrents()
    }

    fun getCurrentByKey(key: Long): LiveData<CurrentEntity> {
        return currentWeatherDao.getCurrentByKey(key)
    }

    fun getCurrentByKName(name: String): LiveData<CurrentEntity> {
        return currentWeatherDao.getCurrentByName(name)
    }

    suspend fun deleteCurrent(key: Long) = withContext(Dispatchers.IO) {
        currentWeatherDao.deleteCurrent(key)
    }

    suspend fun deleteCurrents() = withContext(Dispatchers.IO) {
        currentWeatherDao.deleteCurrents()
    }

    suspend fun searchCurrentByName(name: String): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByName(
                location = name,
                units = units
            )
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByLatLon(
                latitude = location.latitude,
                longitude = location.longitude,
                units = units
            )
        }
    }

    suspend fun searchCurrentByCityId(id: Long): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherById(
                id = id,
                units = units
            )
        }
    }

    suspend fun updateCurrent(key: Long) {
        Log.d("Repository", "update current")
        withContext(Dispatchers.IO) {
            val result = searchCurrentByCityId(key)
            if (result is Result.Success) {
                saveCurrent(result.value.toEntity())
            }
        }
    }

    suspend fun shouldUpdateCurrents() {
        Log.d("Repository", "updating currents")
        if (!preferencesHelper.getAutUpdate() && shouldUpdate(preferencesHelper.getLastUpdate())) {
            Log.d("Repository", "should update currents")
            withContext(Dispatchers.IO) {
                val units = preferencesHelper.getUnits()
                val cityIds = currentWeatherDao.getCityIds()
                val currents = cityIds.map { id ->
                    weatherApiService.getCurrentWeatherById(id = id, units = units)
                }
                for (current in currents) {
                    if (current.id > 0) {
                        val entity = current.toEntity()
                        currentWeatherDao.insertCurrent(entity)
                    }
                }
                preferencesHelper.setLastUpdate(System.currentTimeMillis())
            }
        }
    }

    suspend fun shouldUpdateCurrent(current: CurrentEntity) {
        Log.d("Repository", "updating current")
        if (!preferencesHelper.getAutUpdate() && shouldUpdate(current.deltaTime)) {
            Log.d("Repository", "should update current")
            updateCurrent(current.cityId)
        }
    }
}