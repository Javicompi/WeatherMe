package com.example.android.weatherme.data.network.models.current


data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)