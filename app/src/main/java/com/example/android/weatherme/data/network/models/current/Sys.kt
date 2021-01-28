package com.example.android.weatherme.data.network.models.current


data class Sys(
        val type: Int,
        val id: Int,
        val country: String,
        val sunrise: Int,
        val sunset: Int
)