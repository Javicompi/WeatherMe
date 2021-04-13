package com.example.android.weatherme.data.database.entities.perhour

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "perHours")
data class PerHourEntity(
        @PrimaryKey
        val cityId: Long,
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezoneOffset: Long
) : Parcelable