package com.example.android.weatherme.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.weatherme.data.database.entities.daily.DailyEntity

@Dao
interface DailyDao {

    @Query("SELECT * FROM dailys WHERE `cityId` = :id")
    fun getDailysByKey(id: Long): LiveData<List<DailyEntity>>

    @Query("SELECT * FROM dailys WHERE `cityId` = :id")
    fun getRawDailysByKey(id: Long): List<DailyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailys(dailys: List<DailyEntity>): List<Long>

    @Query("DELETE FROM dailys WHERE `cityId` = :id")
    fun deleteDailys(id: Long)

    @Transaction
    fun updateDailysByKey(dailys: List<DailyEntity>) {
        deleteDailys(dailys[0].cityId)
        insertDailys(dailys)
    }
}