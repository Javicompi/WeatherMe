package com.example.android.weatherme.data.network.models.current

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Clouds(
        val all: Int
)