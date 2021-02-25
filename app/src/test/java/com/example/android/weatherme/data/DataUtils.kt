package com.example.android.weatherme.data

import com.example.android.weatherme.data.network.models.current.NewCurrent
import com.example.android.weatherme.data.network.models.current.NewCurrent.*

fun createCurrent(): NewCurrent {
    return NewCurrent(
        coord = Coord(-0.5192, 38.2246),
        weather = listOf(Weather(800, "Clear", "clear sky", "01n")),
        main = Main(19.46, 13.87, 18.89, 20.00, 1023, 56),
        visibility = 10000,
        wind = Wind(8.23, 310),
        clouds = Clouds(20),
        dt = 1611782073,
        sys = Sys(1, 6391, "ES", 1611731453, 1611767914),
        timezone = 3600,
        id = 6697298,
        name = "Gran Alacant",
        cod = 200,
        base = "stations",
        rain = Rain(oneHour = 0.0, threeHours = 0.0),
        snow = Snow(oneHour = 0.0, threeHours = 0.0)
    )
}