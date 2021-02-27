package com.example.android.weatherme.data.network.api

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class WeatherApiTest : TestCase() {

    @Test
    fun testCurrentByName() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentWeatherByName(
                "gran alacant",
                "metric",
                "es"
            )
        assertThat(result.name, `is`("Gran Alacant"))
    }

    @Test
    fun testCurrentByLatLon() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentWeatherByLatLon(
            38.232394006633555,
            -0.5470151195289806,
            "metric",
            "es"
        )
        assertThat(result.name, `is`("Gran Alacant"))
    }

    @Test
    fun testPerHourByName() = runBlocking {
        val result = WeatherApi.retrofitService.getPerHourByLatLon(
                38.232394006633555,
                -0.5470151195289806,
                "metric",
                "es"
        )
        assertThat(result.hourly.size, `is`(48))
    }
}