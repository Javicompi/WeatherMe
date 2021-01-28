package com.example.android.weatherme.data.network.models.current

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("1h")
    val oneHour: Int? = 0,
    @SerializedName("3h")
    val threeHours: Int? = 0
)