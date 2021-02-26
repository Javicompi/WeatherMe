package com.example.android.weatherme.data.network.models

//import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val cod: Int = 0,
    val message: String = "Unknown Error"
)