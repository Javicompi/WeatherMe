package com.example.android.weatherme.data

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.data.database.CurrentWeatherDao
import com.example.android.weatherme.data.database.PerHourDao
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.perhour.HourlyEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.network.models.perhour.PerHour
import com.example.android.weatherme.data.network.models.perhour.toHourlyEntityList
import com.example.android.weatherme.data.network.models.perhour.toPerHourEntity
import com.example.android.weatherme.utils.PreferencesHelper
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

    suspend fun saveCurrent(current: CurrentEntity): Long = withContext(dbDispatcher) {
        Log.d(TAG, "save current")
        return@withContext currentWeatherDao.insertCurrent(current)
    }

    suspend fun savePerHour(perHour: PerHourEntity): Long = withContext(dbDispatcher) {
        Log.d(TAG, "save perHour")
        return@withContext perHourDao.insertPerHour(perHour)
    }

    suspend fun saveHourlys(hourlys: List<HourlyEntity>): List<Long> = withContext(dbDispatcher) {
        Log.d(TAG, "save hourlys")
        perHourDao.deleteHourlys(hourlys.first().cityId)
        return@withContext perHourDao.insertHourlys(hourlys)
    }

    suspend fun getCurrents(): LiveData<List<CurrentEntity>> = withContext(dbDispatcher) {
        return@withContext currentWeatherDao.getCurrents()
    }

    suspend fun getCurrentByKey(key: Long): LiveData<CurrentEntity> = withContext(dbDispatcher) {
        return@withContext if (key > 0) {
            preferencesHelper.setCurrentSelected(key)
            currentWeatherDao.getCurrentByKey(key)
        } else {
            currentWeatherDao.getCurrentByKey(preferencesHelper.getCurrentSelected())
        }
    }

    suspend fun getPerHourByKey(key: Long): LiveData<PerHourWithHourly> = withContext(dbDispatcher) {
        Log.d(TAG, "getPerHourByKey")
        val cityId = if (key > 0) key else preferencesHelper.getCurrentSelected()
        return@withContext perHourDao.getPerHourbyKey(cityId)
    }

    suspend fun deleteCurrent(key: Long) = withContext(dbDispatcher) {
        preferencesHelper.setCurrentSelected(0)
        currentWeatherDao.deleteCurrent(key)
    }

    suspend fun deletePerHour(key: Long) = withContext(dbDispatcher) {
        perHourDao.deletePerHour(key)
        perHourDao.deleteHourlys(key)
    }

    suspend fun searchCurrentByName(name: String): Result<Current> = withContext(dbDispatcher) {
        return@withContext safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByName(
                    location = name,
                    units = units
            )
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> = withContext(dbDispatcher) {
        return@withContext safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getCurrentWeatherByLatLon(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    units = units
            )
        }
    }

    suspend fun searchPerHourByLatLon(lat: Double, lon: Double): Result<PerHour> = withContext(dbDispatcher) {
        return@withContext safeApiCall(dbDispatcher) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getPerHourByLatLon(
                    latitude = lat,
                    longitude = lon,
                    units = units
            )
        }
    }

    suspend fun updateData() = withContext(dbDispatcher) {
        Log.d(TAG, "updateData")
        val units = preferencesHelper.getUnits()
        val cityIds = currentWeatherDao.getCityIds()
        val currents = cityIds.map { id ->
            weatherApiService.getCurrentWeatherById(id = id, units = units)
        }
        val latLons = mutableListOf<Pair<Double, Double>>()
        for (current in currents) {
            if (current.id > 0) {
                val entity = current.toEntity()
                currentWeatherDao.insertCurrent(entity)
                latLons.add(Pair(entity.latitude, entity.longitude))
            }
        }
        val perHours = latLons.map { latLon ->
            val lat = latLon.first
            val lon = latLon.second
            weatherApiService.getPerHourByLatLon(latitude = lat, longitude = lon, units = units)
        }
        for (i in cityIds.indices) {
            val perHourEntity = perHours[i].toPerHourEntity(cityIds[i])
            perHourDao.insertPerHour(perHourEntity)
            val hourlyEntity = perHours[i].toHourlyEntityList(cityIds[i])
            perHourDao.insertHourlys(hourlyEntity)
        }
        preferencesHelper.setLastUpdate(System.currentTimeMillis())
    }

    suspend fun shouldUpdateCurrents() = withContext(dbDispatcher) {
        Log.d(TAG, "shouldUpdateCurrents")
        if (preferencesHelper.shouldUpdateCurrents()) {
            Log.d(TAG, "should update currents")
            updateData()
        }
    }

    /*suspend fun shouldUpdatePerHour(perHour: PerHourWithHourly) = withContext(dbDispatcher) {
        Log.d(TAG, "shouldUpdatePerHour")
        if (perHour != null && shouldUpdate(perHour.hourlyEntities[0].deltaTime)) {
            Log.d(TAG, "updating")
            val units = preferencesHelper.getUnits()
            val lat = perHour.perHourEntity.lat
            val lon = perHour.perHourEntity.lon
            val cityId = perHour.perHourEntity.cityId
            val result = safeApiCall(dbDispatcher) {
                weatherApiService.getPerHourByLatLon(
                        lat,
                        lon,
                        units
                )
            }
            if (result is Result.Success) {
                perHourDao.insertPerHour(result.value.toPerHourEntity(cityId))
            }
        }
    }*/
}