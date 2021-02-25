package com.example.android.weatherme.data.network.models.current

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Coord(
        val lon: Double,
        val lat: Double
)