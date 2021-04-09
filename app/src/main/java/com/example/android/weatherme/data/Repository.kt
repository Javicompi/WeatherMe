package com.example.android.weatherme.data

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.data.database.CurrentWeatherDao
import com.example.android.weatherme.data.database.PerHourDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.perhour.ApiResponse
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.network.models.perhour.PerHour
import com.example.android.weatherme.data.network.models.perhour.toHourlyEntityList
import com.example.android.weatherme.data.network.models.perhour.toPerHourEntity
import com.example.android.weatherme.utils.PreferencesHelper
import com.example.android.weatherme.utils.shouldUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
        private val dbDispatcher: CoroutineDispatcher,
        private val currentWeatherDao: CurrentWeatherDao,
        private val perHourDao: PerHourDao,
        private val weatherApiService: WeatherApiService,
        private val preferencesHelper: PreferencesHelper
) : BaseRepository() {

    private val TAG = Repository::class.java.simpleName

    fun loadCurrent(id: Long): LiveData<Resource<CurrentEntity>> {
        return object : NetworkBoundResource<CurrentEntity, Current>(dbDispatcher) {
            override fun saveCallResult(item: Current) {
                currentWeatherDao.insertCurrent(item.toEntity())
            }

            override fun shouldFetch(data: CurrentEntity?): Boolean {
                return data != null && shouldUpdate(data.deltaTime)
            }

            override fun loadFromDb(): LiveData<CurrentEntity> {
                val cityId = if (id > 0) {
                    id.also { preferencesHelper.setCurrentSelected(it) }
                } else preferencesHelper.getCurrentSelected()
                return currentWeatherDao.getCurrentByKey(cityId)
            }

            override fun createCall(): LiveData<ApiResponse<Current>> {
                return weatherApiService.getCurrentById(id)
            }
        }.asLiveData()
    }

    fun loadPerHour(id: Long, lat: Double, lon: Double): LiveData<Resource<PerHourWithHourly>> {
        return object : NetworkBoundResource<PerHourWithHourly, PerHour>(dbDispatcher) {
            override fun saveCallResult(item: PerHour) {
                val perHourEntity = item.toPerHourEntity(id)
                val hourlyEntityList = item.toHourlyEntityList(id)
                val perHourWithHourly = PerHourWithHourly(perHourEntity, hourlyEntityList)
                perHourDao.insertPerHour(perHourWithHourly)
            }

            override fun shouldFetch(data: PerHourWithHourly?): Boolean {
                return data != null && shouldUpdate(data.hourlyEntities[0].deltaTime)
            }

            override fun loadFromDb(): LiveData<PerHourWithHourly> {
                val cityId = if (id > 0) id else preferencesHelper.getCurrentSelected()
                return perHourDao.getPerHourbyKey(cityId)
            }

            override fun createCall(): LiveData<ApiResponse<PerHour>> {
                return weatherApiService.getNewPerHourByLatLon(lat, lon)
            }
        }.asLiveData()
    }

    suspend fun saveCurrent(current: CurrentEntity): Long = withContext(dbDispatcher) {
        Log.d(TAG, "save current")
        val id = currentWeatherDao.insertCurrent(current)
        preferencesHelper.setCurrentSelected(id)
        return@withContext id
    }

    suspend fun savePerHourWithHourly(perHour: PerHourWithHourly) = withContext(dbDispatcher) {
        Log.d(TAG, "save perHourWithHourly")
        perHourDao.insertPerHour(perHour)
    }

    suspend fun getCurrents(): LiveData<List<CurrentEntity>> = withContext(dbDispatcher) {
        Log.d(TAG, "getCurrents")
        return@withContext liveData {
            emitSource(currentWeatherDao.getCurrents())
            shouldUpdateCurrents()
        }
    }

    suspend fun getCurrentByKey(key: Long): LiveData<CurrentEntity> = withContext(dbDispatcher) {
        Log.d(TAG, "getCurrentByKey")
        val cityId = if (key > 0) {
            key.also { preferencesHelper.setCurrentSelected(it) }
        } else preferencesHelper.getCurrentSelected()
        return@withContext liveData {
            emitSource(currentWeatherDao.getCurrentByKey(cityId))
        }
    }

    suspend fun getPerHourByKey(key: Long): LiveData<PerHourWithHourly> = withContext(dbDispatcher) {
        Log.d(TAG, "getPerHourByKey")
        val cityId = if (key > 0) key else preferencesHelper.getCurrentSelected()
        return@withContext liveData {
            emitSource(perHourDao.getPerHourbyKey(cityId))
            shouldUpdatePerHour(cityId)
        }
    }

    suspend fun deleteCurrent(key: Long) = withContext(dbDispatcher) {
        Log.d(TAG, "deleteCurrent")
        preferencesHelper.setCurrentSelected(0)
        currentWeatherDao.deleteCurrent(key)
    }

    suspend fun deletePerHour(cityId: Long) = withContext(dbDispatcher) {
        Log.d(TAG, "deletePerHour")
        perHourDao.deletePerHourByKey(cityId)
    }

    suspend fun searchCurrentByName(name: String): Result<Current> = withContext(dbDispatcher) {
        Log.d(TAG, "searchCurrentByName")
        return@withContext safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByName(
                    location = name,
                    units = units
            )
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> = withContext(dbDispatcher) {
        Log.d(TAG, "searchCurrentByLatLon")
        return@withContext safeApiCall(dbDispatcher) {
            weatherApiService.getCurrentWeatherByLatLon(
                    location.latitude,
                    location.longitude,
                    preferencesHelper.getUnits()
            )
        }
    }

    suspend fun searchPerHourByLatLon(lat: Double, lon: Double): Result<PerHour> = withContext(dbDispatcher) {
        Log.d(TAG, "searchPerHourByLatLon")
        return@withContext safeApiCall(dbDispatcher) {
            weatherApiService.getPerHourByLatLon(
                    latitude = lat,
                    longitude = lon,
                    units = preferencesHelper.getUnits()
            )
        }
    }

    suspend fun updateData(forced: Boolean? = false) = withContext(dbDispatcher) {
        Log.d(TAG, "updateData")
        updateCurrents()
        if (forced == true || preferencesHelper.getAutUpdate()) {
            val perHourEntities = perHourDao.getRawPerHours()
            for (perHourWithHourly in perHourEntities) {
                updatePerHour(perHourWithHourly)
            }
        }
    }

    private suspend fun shouldUpdateCurrents() {
        Log.d(TAG, "shouldUpdateCurrents")
        if (!preferencesHelper.getAutUpdate() && shouldUpdate(preferencesHelper.getLastUpdate())) {
            updateCurrents()
        }
    }

    private suspend fun shouldUpdatePerHour(cityId: Long) = withContext(dbDispatcher) {
        Log.d(TAG, "shouldUpdatePerHour")
        val rawHourly = perHourDao.getRawHourlyByKey(cityId)
        if (shouldUpdate(rawHourly.hourlyEntities[0].deltaTime)) {
            updatePerHour(rawHourly)
        }
    }

    private suspend fun updateCurrents() = withContext(dbDispatcher) {
        Log.d(TAG, "updateCurrents")
        val units = preferencesHelper.getUnits()
        val cityIds = currentWeatherDao.getCityIds()
        val currents = cityIds.map { id ->
            safeApiCall(dbDispatcher) {
                weatherApiService.getCurrentWeatherById(id = id, units = units)
            }
        }
        for (current in currents) {
            if (current is Result.Success) {
                val entity = current.value.toEntity()
                currentWeatherDao.insertCurrent(entity)
            }
        }
        preferencesHelper.setLastUpdate(System.currentTimeMillis())
    }

    private suspend fun updatePerHour(perHour: PerHourWithHourly) {
        Log.d(TAG, "updatePerHour")
        val units = preferencesHelper.getUnits()
        val lastUpdate = perHour.hourlyEntities[0].deltaTime
        val newPerHour = safeApiCall(dbDispatcher) {
            val lat = perHour.perHourEntity.lat
            val lon = perHour.perHourEntity.lon
            weatherApiService.getPerHourByLatLon(latitude = lat, longitude = lon, units)
        }
        if (newPerHour is Result.Success && newPerHour.value.hourly[0].dt.toLong() * 1000 > lastUpdate) {
            Log.d(TAG, "updatingPerHour")
            val cityId = perHour.perHourEntity.cityId
            val newPerHourWithHourly = PerHourWithHourly(
                    newPerHour.value.toPerHourEntity(cityId),
                    newPerHour.value.toHourlyEntityList(cityId)
            )
            perHourDao.updatePerHourByKey(newPerHourWithHourly)
        }
    }
}