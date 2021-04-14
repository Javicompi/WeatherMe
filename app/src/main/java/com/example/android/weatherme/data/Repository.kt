package com.example.android.weatherme.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.android.weatherme.data.database.CurrentDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.utils.PreferencesHelper
import com.example.android.weatherme.utils.shouldUpdate
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val currentDao: CurrentDao,
    private val weatherApiService: WeatherApiService,
    private val preferencesHelper: PreferencesHelper
) : BaseRepository() {

    suspend fun saveCurrent(current: CurrentEntity): Long = withContext(ioDispatcher) {
        return@withContext currentDao.insertCurrent(current)
    }

    fun getCurrents(): LiveData<List<CurrentEntity>> {
        return liveData {
            emitSource(currentDao.getCurrents())
            val lastUpdate = preferencesHelper.getLastUpdate()
            if (!preferencesHelper.getAutUpdate() && shouldUpdate(lastUpdate)) {
                updateCurrents()
            }
        }
    }

    fun getCurrentByKey(key: Long): LiveData<CurrentEntity> {
        val cityId = if (key > 0) {
            key.also { preferencesHelper.setCurrentSelected(it) }
        } else {
            preferencesHelper.getCurrentSelected()
        }
        return liveData {
            emitSource(currentDao.getCurrentByKey(cityId))
            shouldUpdateCurrent(cityId)
        }
    }

    suspend fun deleteCurrent(key: Long) = withContext(ioDispatcher) {
        currentDao.deleteCurrent(key)
        preferencesHelper.setCurrentSelected(0)
    }

    suspend fun searchCurrentByName(name: String): Result<Current> = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val response = weatherApiService.getCurrentResponseByName(location = name, units = units)
        return@withContext when(response) {
            is NetworkResponse.Success -> {
                Result.Success(response.body)
            }
            is NetworkResponse.NetworkError -> {
                Result.NetworkError(response.error.localizedMessage)
            }
            is NetworkResponse.ServerError -> {
                Result.GenericError(response.code, response.body)
            }
            is NetworkResponse.UnknownError -> {
                Result.GenericError()
            }
        }
    }

    suspend fun searchCurrentByLocation(location: Location): Result<Current> = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val lat = location.latitude
        val lon = location.longitude
        val response = weatherApiService.getCurrentResponseByLatLon(
                latitude = lat,
                longitude = lon,
                units = units
        )
        return@withContext when(response) {
            is NetworkResponse.Success -> {
                Result.Success(response.body)
            }
            is NetworkResponse.NetworkError -> {
                Result.NetworkError(response.error.localizedMessage)
            }
            is NetworkResponse.ServerError -> {
                Result.GenericError(response.code, response.body)
            }
            is NetworkResponse.UnknownError -> {
                Result.GenericError()
            }
        }
    }

    private suspend fun shouldUpdateCurrent(cityId: Long) = withContext(ioDispatcher) {
        val deltaTime = currentDao.getCurrentDeltaTime(cityId)
        if (shouldUpdate(deltaTime)) {
            updateCurrent(cityId)
        }
    }

    private suspend fun updateCurrent(cityId: Long) = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val current = weatherApiService.getCurrentResponseById(id = cityId, units = units)
        if (current is NetworkResponse.Success) {
            val entity = current.body.toEntity()
            currentDao.insertCurrent(entity)
        }
    }

    suspend fun updateCurrents(forceUpdate: Boolean = false) = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val currents = currentDao.getRawCurrents()
        for (current in currents) {
            if (forceUpdate || shouldUpdate(current.deltaTime)) {
                val newValue = weatherApiService.getCurrentResponseById(
                        id = current.cityId,
                        units = units
                )
                if (newValue is NetworkResponse.Success) {
                    currentDao.insertCurrent(newValue.body.toEntity())
                }
            }
        }
        preferencesHelper.setLastUpdate()
    }
}