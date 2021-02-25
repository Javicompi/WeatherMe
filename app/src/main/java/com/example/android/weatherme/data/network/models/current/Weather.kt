package com.example.android.weatherme.data.network.models.current

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)