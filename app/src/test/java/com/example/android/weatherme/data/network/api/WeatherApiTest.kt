package com.example.android.weatherme.data.network.api

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class WeatherApiTest : TestCase() {

    @Test
    fun testApiByName() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentWeatherByName(
                "gran alacant",
                "metric",
                "es"
            )
        assert(result.name.equals("Gran Alacant"))
    }

    @Test
    fun testApiByLatLon() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentWeatherByLatLon(
            38.232394006633555,
            -0.5470151195289806,
            "metric",
            "es"
        )
        assert(result.name.equals("Gran Alacant"))
    }
}