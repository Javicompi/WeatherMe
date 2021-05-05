package com.example.android.weatherme.data.network.models.perhour

import com.example.android.weatherme.data.database.entities.hourly.HourlyEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
data class PerHour(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        @Json(name = "timezone_offset")
        val timezoneOffset: Int,
        val hourly: List<Hourly>
) {
    @JsonClass(generateAdapter = true)
    data class Hourly(
            val dt: Int,
            val temp: Double,
            @Json(name = "feels_like")
            val feelsLike: Double,
            val pressure: Int,
            val humidity: Int,
            @Json(name = "dew_point")
            val dewPoint: Double,
            val uvi: Double,
            val clouds: Int,
            val visibility: Int,
            @Json(name = "wind_speed")
            val windSpeed: Double,
            @Json(name = "wind_deg")
            val windDeg: Int,
            val weather: List<Weather>,
            val pop: Double,
            val rain: Rain? = null,
            val snow: Snow? = null
    ) {
        @JsonClass(generateAdapter = true)
        data class Weather(
                val id: Int,
                val main: String,
                val description: String,
                val icon: String
        )

        @JsonClass(generateAdapter = true)
        data class Rain(
                @Json(name = "1h")
                val oneHour: Double? = null,
                @Json(name = "3h")
                val threeHours: Double? = null
        )

        @JsonClass(generateAdapter = true)
        data class Snow(
                @Json(name = "1h")
                val oneHour: Double? = null,
                @Json(name = "3h")
                val threeHours: Double? = null
        )
    }
}

fun PerHour.toHourlyEntityList(cityId: Long): List<HourlyEntity> {
    return hourly.map {
        HourlyEntity(
                id = null,
                cityId = cityId,
                lat = lat,
                lon = lon,
                deltaTime = it.dt.toLong() * 1000,
                timezone = timezone,
                offset = timezoneOffset * 1000,
                temp = it.temp.roundToInt(),
                feelsLike = it.temp.roundToInt(),
                pressure = it.pressure,
                humidity = it.humidity,
                dewPoint = it.dewPoint,
                uvi = it.uvi,
                clouds = it.clouds,
                visibility = it.visibility,
                windSpeed = it.windSpeed.roundToInt(),
                windDegrees = it.windDeg,
                pop = (it.pop * 100).toInt(),
                rainOneHour = it.rain?.oneHour ?: 0.0,
                rainThreeHours = it.rain?.threeHours ?: 0.0,
                snowOneHour = it.snow?.oneHour ?: 0.0,
                snowThreeHours = it.snow?.threeHours ?: 0.0,
                weatherId = it.weather[0].id,
                shortDescription = it.weather[0].main.capitalize(Locale.getDefault()),
                description = it.weather[0].description.capitalize(Locale.getDefault()),
                icon = "_" + it.weather[0].icon
        )
    }
}