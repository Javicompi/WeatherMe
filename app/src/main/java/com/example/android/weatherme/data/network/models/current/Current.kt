package com.example.android.weatherme.data.network.models.current


import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
data class Current(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
) {
    @JsonClass(generateAdapter = true)
    data class Coord(
        val lon: Double,
        val lat: Double
    )

    @JsonClass(generateAdapter = true)
    data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    )

    @JsonClass(generateAdapter = true)
    data class Main(
        val temp: Double,
        @Json(name = "feels_like")
        val feelsLike: Double,
        @Json(name = "temp_min")
        val tempMin: Double,
        @Json(name = "temp_max")
        val tempMax: Double,
        val pressure: Int,
        val humidity: Int
    )

    @JsonClass(generateAdapter = true)
    data class Wind(
        val speed: Double,
        val deg: Int
    )

    @JsonClass(generateAdapter = true)
    data class Clouds(
        val all: Int
    )

    @JsonClass(generateAdapter = true)
    data class Sys(
        val type: Int,
        val id: Int,
        val country: String,
        val sunrise: Int,
        val sunset: Int
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

fun Current.toEntity(): CurrentEntity {
    return CurrentEntity(
        latitude = coord.lat,
        longitude = coord.lon,
        weatherId = weather[0].id,
        shortDescription = weather[0].main.capitalize(Locale.getDefault()),
        description = weather[0].description.capitalize(Locale.getDefault()),
        icon = "_" + weather[0].icon,
        temp = main.temp.roundToInt(),
        tempMin = main.temp.roundToInt(),
        tempMax = main.tempMax.roundToInt(),
        feelsLike = main.feelsLike.roundToInt(),
        pressure = main.pressure,
        humidity = main.humidity,
        windSpeed = wind.speed.roundToInt(),
        windDegrees = wind.deg,
        clouds = clouds.all,
        rainOneHour = rain?.oneHour ?: 0.0,
        rainThreeHours = rain?.threeHours ?: 0.0,
        snowOneHour = snow?.oneHour ?: 0.0,
        snowThreeHours = snow?.threeHours ?: 0.0,
        country = sys.country,
        timeZone = timezone * 1000,
        sunrise = (sys.sunrise.toLong() * 1000),
        sunset = (sys.sunset.toLong() * 1000),
        cityId = id.toLong(),
        cityName = name,
        deltaTime = dt.toLong() * 1000,
        visibility = visibility
    )
}