package com.example.android.weatherme.data.network.models.daily


import com.example.android.weatherme.data.database.entities.daily.DailyEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
data class PerDay(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @Json(name = "timezone_offset")
    val timezoneOffset: Int,
    val daily: List<Daily>
) {
    @JsonClass(generateAdapter = true)
    data class Daily(
        val dt: Int,
        val sunrise: Int,
        val sunset: Int,
        val moonrise: Int,
        val moonset: Int,
        @Json(name = "moon_phase")
        val moonPhase: Double,
        val temp: Temp,
        @Json(name = "feels_like")
        val feelsLike: FeelsLike,
        val pressure: Int,
        val humidity: Int,
        @Json(name = "dew_point")
        val dewPoint: Double,
        @Json(name = "wind_speed")
        val windSpeed: Double,
        @Json(name = "wind_deg")
        val windDeg: Int,
        @Json(name = "wind_gust")
        val windGust: Double? = null,
        val weather: List<Weather>,
        val clouds: Int,
        val pop: Double,
        val rain: Double? = null,
        val snow: Double? = null,
        val uvi: Double
    ) {
        @JsonClass(generateAdapter = true)
        data class Temp(
            val day: Double,
            val min: Double,
            val max: Double,
            val night: Double,
            val eve: Double,
            val morn: Double
        )

        @JsonClass(generateAdapter = true)
        data class FeelsLike(
            val day: Double,
            val night: Double,
            val eve: Double,
            val morn: Double
        )

        @JsonClass(generateAdapter = true)
        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )
    }
}

fun PerDay.toDailyEntityList(cityId: Long): List<DailyEntity> {
    return daily.map {
        DailyEntity(
            id = null,
            cityId = cityId,
            lat = lat,
            lon = lon,
            deltaTime = it.dt.toLong() * 1000,
            timezone = timezone,
            offset = timezoneOffset,
            sunrise = it.sunrise.toLong() * 1000,
            sunset = it.sunset.toLong() * 1000,
            moonrise = it.moonrise.toLong() * 1000,
            moonset = it.moonset.toLong() * 1000,
            moonPhase = it.moonPhase,
            tempMax = it.temp.max.roundToInt(),
            tempMin = it.temp.min.roundToInt(),
            tempMorn = it.temp.morn.roundToInt(),
            tempDay = it.temp.day.roundToInt(),
            tempNight = it.temp.night.roundToInt(),
            tempEve = it.temp.eve.roundToInt(),
            feelsLikeMorn = it.feelsLike.morn.roundToInt(),
            feelsLikeDay = it.feelsLike.day.roundToInt(),
            feelsLikeNight = it.feelsLike.night.roundToInt(),
            feelsLikeEve = it.feelsLike.eve.roundToInt(),
            pressure = it.pressure,
            humidity = it.humidity,
            dewPoint = it.dewPoint,
            windSpeed = it.windSpeed.roundToInt(),
            windDegrees = it.windDeg,
            windGust = it.windGust?.toInt() ?: 0,
            clouds = it.clouds,
            pop = (it.pop * 100).roundToInt(),
            rain = it.rain ?: 0.0,
            snow = it.snow ?: 0.0,
            uvi = it.uvi,
            weatherId = it.weather[0].id,
            description = it.weather[0].description,
            shortDescription = it.weather[0].main,
            icon = "_" + it.weather[0].icon
        )
    }
}