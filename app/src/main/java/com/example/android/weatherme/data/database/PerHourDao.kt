package com.example.android.weatherme.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.weatherme.data.database.entities.perhour.HourlyEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly

@Dao
interface PerHourDao {

    @Transaction
    @Query("SELECT * FROM perHours WHERE `cityId` = :id")
    fun getPerHourbyKey(id: Long): LiveData<PerHourWithHourly>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerHour(perHourEntity: PerHourEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHourlys(hourlys: List<HourlyEntity>): List<Long>

    @Query("DELETE FROM perHours WHERE `cityId` = :id")
    fun deletePerHour(id: Long)

    @Query("DELETE FROM hourlys WHERE `cityId` = :id")
    fun deleteHourlys(id: Long)
}