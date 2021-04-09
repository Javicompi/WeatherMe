package com.example.android.weatherme.data

import android.location.Location
import androidx.lifecycle.LiveData
import com.example.android.weatherme.data.database.CurrentWeatherDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.utils.PreferencesHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
        private val dbDispatcher: CoroutineDispatcher,
        private val currentWeatherDao: CurrentWeatherDao,
        private val weatherApiService: WeatherApiService,
        private val preferencesHelper: PreferencesHelper
) : BaseRepository() {

    suspend fun saveCurrent(current: CurrentEntity): Long {
        var id: Long
        withContext(dbDispatcher) {
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

    suspend fun deleteCurrent(key: Long) = withContext(dbDispatcher) {
        currentWeatherDao.deleteCurrent(key)
    }

    suspend fun deleteCurrents() = withContext(dbDispatcher) {
        currentWeatherDao.deleteCurrents()
    }

    suspend fun searchCurrentByName(name: String): Result<Current> {
        return safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByName(
                location = name,
                units = units
            )
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> {
        return safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByLatLon(
                latitude = location.latitude,
                longitude = location.longitude,
                units = units
            )
        }
    }

    suspend fun updateCurrents() {
        withContext(dbDispatcher) {
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
        }
    }
}