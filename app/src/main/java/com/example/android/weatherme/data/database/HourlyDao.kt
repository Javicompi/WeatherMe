package com.example.android.weatherme.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.weatherme.data.database.entities.hourly.HourlyEntity

@Dao
interface HourlyDao {

    @Query("SELECT * FROM hourlys WHERE `cityId` = :id")
    fun getHourlysByKey(id: Long): LiveData<List<HourlyEntity>>

    @Query("SELECT * FROM hourlys WHERE `cityId` =:id")
    fun getRawHourlysByKey(id: Long): List<HourlyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHourlys(hourlys: List<HourlyEntity>): List<Long>

    @Query("DELETE FROM hourlys WHERE cityId = :id")
    fun deleteHourlys(id: Long)

    @Transaction
    fun updateHourlysByKey(hourlys: List<HourlyEntity>) {
        deleteHourlys(hourlys[0].cityId)
        insertHourlys(hourlys)
    }

    @Query("SELECT deltaTime FROM hourlys WHERE cityId =:cityId ORDER BY deltaTime ASC ")
    fun getHourlyDeltaTime(cityId: Long): Long
}