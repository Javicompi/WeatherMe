package com.example.android.weatherme.data.network.models.daily


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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