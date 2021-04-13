package com.example.android.weatherme.data.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.invoke
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class WeatherApiTest : TestCase() {

    @Test
    fun testApiByName() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentResponseByName(
                "gran alacant",
                "metric",
                "es"
            )
        assert(result is NetworkResponse.Success)
        assertThat(result.invoke()?.name, `is`("Gran Alacant"))
    }

    @Test
    fun testApiByLatLon() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentResponseByLatLon(
            38.232394006633555,
            -0.5470151195289806,
            "metric",
            "es"
        )
        assert(result is NetworkResponse.Success)
        assertThat(result.invoke()?.name, `is`("Gran Alacant"))
    }

    @Test
    fun testApiResponseByName() = runBlocking {
        val result = WeatherApi.retrofitService.getCurrentResponseById(
            6697298,
            "metric",
            "es"
        )
        assert(result is NetworkResponse.Success)
        assertThat(result.invoke()?.name, `is`("Gran Alacant"))
    }
}