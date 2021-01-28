package com.example.android.weatherme.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.weatherme.data.database.entities.current.CurrentEntity

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM currents")
    suspend fun getCurrents(): List<CurrentEntity>

    @Query("SELECT * FROM currents WHERE cityName = :cityName")
    suspend fun getCurrentByName(cityName: String): CurrentEntity

    @Query("SELECT * FROM currents WHERE `key` = :key")
    suspend fun getCurrentByKey(key: Long): CurrentEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(currentEntity: CurrentEntity): Long

    @Query("DELETE FROM currents")
    suspend fun deleteCurrents()
}