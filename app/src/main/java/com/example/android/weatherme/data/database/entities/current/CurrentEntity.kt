package com.example.android.weatherme.data.database.entities.current

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currents")
data class CurrentEntity(
    @PrimaryKey(autoGenerate = true)
    var key: Long = -1,
    val latitude: Double,
    val longitude: Double,
    val weatherId: Int,
    val shortDescription: String,
    val description: String,
    val icon: String,
    val temp: Int,
    val tempMin: Int,
    val tempMax: Int,
    val feelsLike: Int,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Int,
    val windDegrees: Int,
    val clouds: Int,
    val rainOneHour: Int = 0,
    val rainThreeHours: Int = 0,
    val snowOneHour: Int = 0,
    val snowThreeHours: Int = 0,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
    val timeZone: Int,
    val cityId: Int,
    val cityName: String,
    val deltaTime: Long,
    val visibility: Int
) {
    fun isSuccess() : Boolean = key >= 0
}