package com.example.android.weatherme.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "dailys")
data class DailyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val cityId: Long,
    val lat: Double,
    val lon: Double,
    val deltaTime: Long,
    val timezone: String,
    val offset: Int,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val tempMax: Int,
    val tempMin: Int,
    val tempMorn: Int,
    val tempDay: Int,
    val tempNight: Int,
    val tempEve: Int,
    val feelsLikeMorn: Int,
    val feelsLikeDay: Int,
    val feelsLikeNight: Int,
    val feelsLikeEve: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val windSpeed: Int,
    val windDegrees: Int,
    val windGust: Int,
    val clouds: Int,
    val pop: Int,
    val rain: Double,
    val snow: Double,
    val uvi: Double,
    val weatherId: Int,
    val description: String,
    val shortDescription: String,
    val icon: String
) : Parcelable