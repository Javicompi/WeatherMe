package com.example.android.weatherme.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hourlys")
data class HourlyEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,
        val cityId: Long,
        val lat: Double,
        val lon: Double,
        val deltaTime: Long,
        val timezone: String,
        val offset: Int,
        val temp: Int,
        val feelsLike: Int,
        val pressure: Int,
        val humidity: Int,
        val dewPoint: Double,
        val uvi: Double,
        val clouds: Int,
        val visibility: Int,
        val windSpeed: Int,
        val windDegrees: Int,
        val pop: Int,
        val rainOneHour: Double = 0.0,
        val rainThreeHours: Double = 0.0,
        val snowOneHour: Double = 0.0,
        val snowThreeHours: Double = 0.0,
        val weatherId: Int,
        val shortDescription: String,
        val description: String,
        val icon: String,
) : Parcelable