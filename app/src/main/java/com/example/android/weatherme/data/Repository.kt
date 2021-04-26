package com.example.android.weatherme.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.android.weatherme.data.database.CurrentDao
import com.example.android.weatherme.data.database.HourlyDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.perhour.HourlyEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.network.models.perhour.toHourlyEntityList
import com.example.android.weatherme.utils.PreferencesHelper
import com.example.android.weatherme.utils.createDefaultHourlys
import com.example.android.weatherme.utils.shouldUpdate
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.*
import javax.inject.Inject

class Repository @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val currentDao: CurrentDao,
        private val hourlyDao: HourlyDao,
        private val weatherApiService: WeatherApiService,
        private val preferencesHelper: PreferencesHelper
) {

    suspend fun saveCurrent(current: CurrentEntity): Long = withContext(ioDispatcher) {
        val id = currentDao.insertCurrent(current)
        val defaultHourlys = createDefaultHourlys(current)
        hourlyDao.insertHourlys(defaultHourlys)
        return@withContext id
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

    fun getHourlys(): LiveData<List<HourlyEntity>> {
        return liveData {
            val cityId = preferencesHelper.getCurrentSelected()
            emitSource(hourlyDao.getHourlysByKey(cityId))
            shouldUpdateHourlys(cityId)
        }
    }

    /*fun getHourlys(): LiveData<List<HourlyEntity>> {
        val mediatorLiveData = MediatorLiveData<List<HourlyEntity>>()
        val cityId = preferencesHelper.getCurrentSelected()
        mediatorLiveData.addSource(hourlyDao.getHourlysByKey(cityId)) { hourlyList ->
            val updateTime = hourlyList[0].deltaTime
            if (shouldUpdate(updateTime)) {
                CoroutineScope(ioDispatcher).launch {
                    updatePerHour(cityId)
                }
            }
        }
        return mediatorLiveData
    }*/

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
        hourlyDao.deleteHourlys(key)
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

    private suspend fun shouldUpdateHourlys(cityId: Long) = withContext(ioDispatcher) {
        val deltaTime = hourlyDao.getHourlyDeltaTime(cityId)
        if (shouldUpdate(deltaTime)) {
            updatePerHour(cityId)
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
                val newCurrent = weatherApiService.getCurrentResponseById(
                        id = current.cityId,
                        units = units
                )
                if (newCurrent is NetworkResponse.Success) {
                    currentDao.insertCurrent(newCurrent.body.toEntity())
                }
            }
            if (forceUpdate) {
                updatePerHour(current.cityId)
            }
        }
        preferencesHelper.setLastUpdate()
    }

    private suspend fun updatePerHour(cityId: Long) = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val current = currentDao.getRawCurrentByKey(cityId)
        val lat = current.latitude
        val lon = current.longitude
        val newValue = weatherApiService.getPerHourByLatLon(
                latitude = lat,
                longitude = lon,
                units = units
        )
        if (newValue is NetworkResponse.Success) {
            val hourlys = newValue.body.toHourlyEntityList(current.cityId)
            hourlyDao.updateHourlysByKey(hourlys)
        }
    }
}