package com.example.android.weatherme.data.database.entities.current

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "currents")
data class CurrentEntity(
    val latitude: Double,
    val longitude: Double,
    val weatherId: Int,
    val shortDescription: String?,
    val description: String?,
    val icon: String?,
    val temp: Int,
    val tempMin: Int,
    val tempMax: Int,
    val feelsLike: Int,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Int,
    val windDegrees: Int,
    val clouds: Int,
    val rainOneHour: Double = 0.0,
    val rainThreeHours: Double = 0.0,
    val snowOneHour: Double = 0.0,
    val snowThreeHours: Double = 0.0,
    val country: String?,
    val sunrise: Long,
    val sunset: Long,
    val timeZone: Int,
    @PrimaryKey
    val cityId: Long,
    val cityName: String?,
    val deltaTime: Long,
    val visibility: Int,
) : Parcelable