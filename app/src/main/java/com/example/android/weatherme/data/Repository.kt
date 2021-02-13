package com.example.android.weatherme.data

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.api.WeatherApi
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class Repository(private val database: WeatherDatabase) : BaseRepository() {

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

    suspend fun searchCurrentByName(name: String): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherByName(name)
        }
    }

    suspend fun searchCurrentByLatLon(location: Location): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherByLatLon(location.latitude, location.longitude)
        }
    }

    suspend fun searchCurrentByCityId(id: Long): Result<Current> {
        return safeApiCall(Dispatchers.IO) {
            return@safeApiCall WeatherApi.retrofitService.getCurrentWeatherById(id)
        }
    }

    /*suspend fun updateCurrents() {
        val currents = database.currentWeatherDao().getCurrents()
        if (currents.value != null && currents.value?.isNotEmpty() == true) {
            for (current in currents.value!!) {
                Log.d(TAG, "updating ${current.cityName}")
                val updated = safeApiCall(Dispatchers.Main) {
                    current.cityName?.let { WeatherApi.retrofitService.getCurrentWeatherByName(it) }
                }
                updated.let {
                    if (updated is Result.Success) {
                        updated.value?.let { it1 -> database.currentWeatherDao().insertCurrent(it1.toEntity()) }
                    }
                }
            }
        }
    }*/

    suspend fun updateCurrents() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "updatingCurrents")
            val cityIds = database.currentWeatherDao().getCityIds()
            Log.d(TAG, "ids in database: ${cityIds.size}")
            val currents = cityIds.map { id ->
                WeatherApi.retrofitService.getCurrentWeatherById(id)
            }
            Log.d(TAG, "downloaded values: ${currents.size}")
            for (current in currents) {
                if (current.id > 0) {
                    val entity = current.toEntity()
                    database.currentWeatherDao().insertCurrent(entity)
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