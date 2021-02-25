package com.example.android.weatherme.data.network.models.current

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Snow(
    @Json(name = "1h")
    val oneHour: Int? = 0,
    @Json(name = "3h")
    val threeHours: Int? = 0
)