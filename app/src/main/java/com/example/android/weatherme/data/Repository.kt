package com.example.android.weatherme.data

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.android.weatherme.data.database.CurrentDao
import com.example.android.weatherme.data.database.HourlyDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.hourly.HourlyEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.ErrorResponse
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.network.models.perhour.PerHour
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

    fun getCurrentByKey(key: Long): LiveData<CurrentEntity> {

        return object :
            NetworkBoundResource<CurrentEntity, NetworkResponse<Current, ErrorResponse>>() {

            override fun processResponse(response: NetworkResponse<Current, ErrorResponse>): CurrentEntity {
                Log.d("NetworkBoundResource", "processResponse")
                return (response as NetworkResponse.Success).body.toEntity()
            }

            override suspend fun saveResult(item: CurrentEntity): Unit = withContext(ioDispatcher) {
                Log.d("NetworkBoundResource", "saveResult")
                currentDao.insertCurrent(item)
            }

            override fun shouldFetch(data: CurrentEntity?): Boolean {
                Log.d("NetworkBoundResource", "shouldFetch")
                return if (data != null && !preferencesHelper.getAutUpdate()) {
                    shouldUpdate(data.deltaTime)
                } else {
                    false
                }
            }

            override suspend fun loadFromDb(): CurrentEntity = withContext(ioDispatcher) {
                Log.d("NetworkBoundResource", "loadFromDb")
                val id = if (key > 0) {
                    key.also { preferencesHelper.setCurrentSelected(it) }
                } else {
                    preferencesHelper.getCurrentSelected()
                }
                return@withContext currentDao.getRawCurrentByKey(id)
            }

            override suspend fun createCall(data: CurrentEntity): NetworkResponse<Current, ErrorResponse> {
                Log.d("NetworkBoundResource", "createCall")
                val units = preferencesHelper.getUnits()
                return weatherApiService.getCurrentResponseById(id = data.cityId, units = units)
            }
        }.asLiveData()
    }

    fun getHourlys(): LiveData<List<HourlyEntity>> {

        return object :
            NetworkBoundResource<List<HourlyEntity>, NetworkResponse<PerHour, ErrorResponse>>() {

            override fun processResponse(response: NetworkResponse<PerHour, ErrorResponse>): List<HourlyEntity> {
                Log.d("NetworkBoundResource", "processResponse")
                val id = preferencesHelper.getCurrentSelected()
                return (response as NetworkResponse.Success).body.toHourlyEntityList(id)
            }

            override suspend fun saveResult(item: List<HourlyEntity>): Unit =
                withContext(ioDispatcher) {
                    Log.d("NetworkBoundResource", "saveResult")
                    hourlyDao.updateHourlysByKey(item)
                }

            override fun shouldFetch(data: List<HourlyEntity>?): Boolean {
                Log.d("NetworkBoundResource", "shouldFetch")
                return if (!data.isNullOrEmpty()) {
                    shouldUpdate(data[0].deltaTime)
                } else {
                    false
                }
            }

            override suspend fun loadFromDb(): List<HourlyEntity> = withContext(ioDispatcher) {
                Log.d("NetworkBoundResource", "loadFromDb")
                val id = preferencesHelper.getCurrentSelected()
                return@withContext hourlyDao.getRawHourlysByKey(id)
            }

            override suspend fun createCall(data: List<HourlyEntity>): NetworkResponse<PerHour, ErrorResponse> {
                Log.d("NetworkBoundResource", "createCall")
                val lat = data[0].lat
                val lon = data[0].lon
                val units = preferencesHelper.getUnits()
                return weatherApiService.getPerHourByLatLon(
                    latitude = lat,
                    longitude = lon,
                    units = units
                )
            }
        }.asLiveData()
    }

    suspend fun deleteCurrent(key: Long) = withContext(ioDispatcher) {
        currentDao.deleteCurrent(key)
        hourlyDao.deleteHourlys(key)
        preferencesHelper.setCurrentSelected(0)
    }

    suspend fun searchCurrentByName(name: String): Result<Current> = withContext(ioDispatcher) {
        val units = preferencesHelper.getUnits()
        val response = weatherApiService.getCurrentResponseByName(location = name, units = units)
        return@withContext when (response) {
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

    suspend fun searchCurrentByLocation(location: Location): Result<Current> =
        withContext(ioDispatcher) {
            val units = preferencesHelper.getUnits()
            val lat = location.latitude
            val lon = location.longitude
            val response = weatherApiService.getCurrentResponseByLatLon(
                latitude = lat,
                longitude = lon,
                units = units
            )
            return@withContext when (response) {
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