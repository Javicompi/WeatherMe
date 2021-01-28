package com.example.android.weatherme.data.network.models.current

import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import kotlin.math.roundToInt

data class Current(
        val coord: Coord,
        val weather: List<Weather>,
        val base: String,
        val main: Main,
        val visibility: Int,
        val wind: Wind,
        val clouds: Clouds,
        val rain: Rain?,
        val snow: Snow?,
        val dt: Int,
        val sys: Sys,
        val timezone: Int,
        val id: Int,
        val name: String,
        val cod: Int
)

fun Current.toEntity(): CurrentEntity {
        return CurrentEntity(
                latitude = coord.lat,
                longitude = coord.lon,
                weatherId = weather[0].id,
                shortDescription = weather[0].main.capitalize(),
                description = weather[0].description.capitalize(),
                icon = weather[0].icon,
                temp = main.temp.roundToInt(),
                tempMin = main.temp.roundToInt(),
                tempMax = main.tempMax.roundToInt(),
                feelsLike = main.feelsLike.roundToInt(),
                pressure = main.pressure,
                humidity = main.humidity,
                windSpeed = wind.speed.roundToInt(),
                windDegrees = wind.deg,
                clouds = clouds.all,
                rainOneHour = rain?.oneHour ?: 0,
                rainThreeHours = rain?.threeHours ?: 0,
                snowOneHour = snow?.oneHour ?: 0,
                snowThreeHours = snow?.threeHours ?: 0,
                country = sys.country,
                sunrise = sys.sunrise.toLong() * 1000,
                sunset = sys.sunset.toLong() * 1000,
                timeZone = timezone,
                cityId = id,
                cityName = name,
                deltaTime = dt.toLong() * 1000,
                visibility = visibility
        )
}