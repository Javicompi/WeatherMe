package com.example.android.weatherme.data.network.models.current

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wind(
        val speed: Double,
        val deg: Int
)