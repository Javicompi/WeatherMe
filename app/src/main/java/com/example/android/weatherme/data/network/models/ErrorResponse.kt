package com.example.android.weatherme.data.network.models

//import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val cod: Int,
    val message: String
)