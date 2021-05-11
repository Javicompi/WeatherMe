package com.example.android.weatherme.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.weatherme.data.database.entities.current.CurrentEntity

@Dao
interface CurrentDao {

    @Query("SELECT * FROM currents")
    fun getCurrents(): LiveData<List<CurrentEntity>>

    @Query("SELECT * FROM currents")
    fun getRawCurrents(): List<CurrentEntity>

    @Query("SELECT * FROM currents WHERE cityName = :cityName")
    fun getCurrentByName(cityName: String): LiveData<CurrentEntity>

    @Query("SELECT * FROM currents WHERE cityId = :cityId")
    fun getCurrentByKey(cityId: Long): LiveData<CurrentEntity>

    @Query("SELECT * FROM currents WHERE cityId = :cityId")
    fun getRawCurrentByKey(cityId: Long): CurrentEntity

    @Query("SELECT deltaTime FROM currents WHERE cityId = :cityId")
    fun getCurrentDeltaTime(cityId: Long): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrent(currentEntity: CurrentEntity): Long

    @Query("DELETE FROM currents WHERE `cityId` = :cityId")
    fun deleteCurrent(cityId: Long)

    @Query("SELECT cityId FROM currents")
    fun getCityIds(): List<Long>
}