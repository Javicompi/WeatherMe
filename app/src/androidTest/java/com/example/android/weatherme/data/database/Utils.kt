package com.example.android.weatherme.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.Current.*
import com.example.android.weatherme.data.network.models.perhour.PerHour
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun createCurrent(name: String = "Gran Alacant", cityId: Int = 6697298): Current {
    return Current(
        coord = Coord(-0.5192, 38.2246),
        weather = listOf<Weather>(Weather(800, "Clear", "clear sky", "01n")),
        main = Main(19.46, 13.87, 18.89, 20.00, 1023, 56),
        visibility = 10000,
        wind = Wind(8.23, 310),
        clouds = Clouds(20),
        dt = 1611782073,
        sys = Sys("ES", 1611731453, 1611767914),
        timezone = 3600,
        id = cityId,
        name = name,
        cod = 200,
        base = "stations",
        rain = Rain(oneHour = 0.0, threeHours = 0.0),
        snow = Snow(oneHour = 0.0, threeHours = 0.0)
    )
}

fun createPerHour(lat: Double = 38.2246, lon: Double = -0.5192): PerHour {
    return PerHour(
            lat = lat,
            lon = lon,
            timezone = "Europe/Madrid",
            timezoneOffset = 3600,
            hourly = createHourlys()
    )
}

private fun createHourlys(): List<PerHour.Hourly> {
    val hourlys = mutableListOf<PerHour.Hourly>()
    repeat(48) {
        val hourly = PerHour.Hourly(
                dt = 1614535200,
                temp = 13.49,
                feelsLike = 6.33,
                pressure = 1021,
                humidity = 77,
                dewPoint = 9.54,
                uvi = 0.0,
                clouds = 20,
                visibility = 10000,
                windSpeed = 10.12,
                windDeg = 62,
                pop = 0.0,
                weather = listOf(createHourlyWeather())
        )
        hourlys.add(hourly)
    }
    return hourlys
}

private fun createHourlyWeather(): PerHour.Hourly.Weather {
    return PerHour.Hourly.Weather(
            id = 801,
            main = "Clouds",
            description = "few clouds",
            icon = "02n"
    )
}

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}