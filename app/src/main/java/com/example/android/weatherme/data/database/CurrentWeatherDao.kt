package com.example.android.weatherme.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM currents")
    fun getCurrents(): LiveData<List<CurrentEntity>>

    @Query("SELECT * FROM currents WHERE cityName = :cityName")
    fun getCurrentByName(cityName: String): CurrentEntity

    @Query("SELECT * FROM currents WHERE `cityId` = :id")
    fun getCurrentByKey(id: Long): LiveData<CurrentEntity>

    @Query("SELECT * FROM currents LIMIT 1")
    fun getFirstCurrent(): CurrentEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrent(currentEntity: CurrentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrents(currents: List<CurrentEntity>)

    @Query("DELETE FROM currents WHERE `cityId` = :id")
    fun deleteCurrent(id: Long)

    @Query("DELETE FROM currents")
    fun deleteCurrents()

    @Query("SELECT COUNT() FROM currents WHERE cityName = :cityName")
    fun count(cityName: String): Int

    @Query("SELECT cityId FROM currents")
    fun getCityIds(): List<Long>
}