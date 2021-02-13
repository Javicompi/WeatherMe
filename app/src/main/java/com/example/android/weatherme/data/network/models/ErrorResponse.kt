package com.example.android.weatherme.data.network.models


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    val cod: Int,
    val message: String
)