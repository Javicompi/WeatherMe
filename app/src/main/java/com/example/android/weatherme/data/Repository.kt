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
import com.example.android.weatherme.utils.PreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val currentWeatherDao: CurrentWeatherDao,
    private val perHourDao: PerHourDao,
    private val weatherApiService: WeatherApiService,
    private val preferencesHelper: PreferencesHelper
) : BaseRepository() {

    private val TAG = Repository::class.java.simpleName

    suspend fun saveCurrent(current: CurrentEntity): Long {
        Log.d(TAG, "save current")
        var id: Long
        withContext(Dispatchers.IO) {
            id = currentWeatherDao.insertCurrent(current)
        }
        return id
    }

    suspend fun savePerHour(perHour: PerHourEntity): Long {
        Log.d(TAG, "save perHour")
        var id: Long
        withContext(Dispatchers.IO) {
            id = perHourDao.insertPerHour(perHour)
        }
        return id
    }

    suspend fun saveHourlys(hourlys: List<HourlyEntity>): List<Long> {
        Log.d(TAG, "save hourlys")
        var id: List<Long>
        withContext(Dispatchers.IO) {
            perHourDao.deleteHourlys(hourlys.first().cityId)
            id = perHourDao.insertHourlys(hourlys)
        }
        return id
    }

    fun getCurrents(): LiveData<List<CurrentEntity>> {
        return currentWeatherDao.getCurrents()
    }

    fun getCurrentByKey(key: Long): LiveData<CurrentEntity> {
        if (key > 0) {
            preferencesHelper.setCurrentSelected(key)
            return currentWeatherDao.getCurrentByKey(key)
        } else {
            return currentWeatherDao.getCurrentByKey(preferencesHelper.getCurrentSelected())
        }
    }

    fun getPerHourByKey(key: Long): LiveData<PerHourWithHourly> {
        if (key > 0) {
            return perHourDao.getPerHourbyKey(key)
        } else {
            return perHourDao.getPerHourbyKey(preferencesHelper.getCurrentSelected())
        }
    }

    fun getCurrentByKName(name: String): LiveData<CurrentEntity> {
        return currentWeatherDao.getCurrentByName(name)
    }

    suspend fun deleteCurrent(key: Long) = withContext(Dispatchers.IO) {
        preferencesHelper.setCurrentSelected(0)
        currentWeatherDao.deleteCurrent(key)
    }

    suspend fun deleteCurrents() = withContext(Dispatchers.IO) {
        preferencesHelper.setCurrentSelected(0)
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
            return@safeApiCall weatherApiService.getCurrentWeatherById(id = id, units = units)
        }
    }

    suspend fun searchPerHourByLatLon(lat: Double, lon: Double): Result<PerHour> {
        return safeApiCall(Dispatchers.IO) {
            val units = preferencesHelper.getUnits()
            return@safeApiCall weatherApiService.getPerHourByLatLon(
                    latitude = lat,
                    longitude = lon,
                    units = units
            )
        }
    }

    suspend fun updateCurrent(key: Long) {
        Log.d(TAG, "update current")
        withContext(Dispatchers.IO) {
            val result = searchCurrentByCityId(key)
            if (result is Result.Success) {
                saveCurrent(result.value.toEntity())
            }
        }
    }

    suspend fun updateCurrents() {
        Log.d(TAG, "updateCurrents")
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

    suspend fun shouldUpdateCurrents() {
        Log.d(TAG, "shouldUpdateCurrents")
        if (preferencesHelper.shouldUpdateCurrents()) {
            Log.d(TAG, "should update currents")
            withContext(Dispatchers.IO) {
                updateCurrents()
            }
        }
    }
}