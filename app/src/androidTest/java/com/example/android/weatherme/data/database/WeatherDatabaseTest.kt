package com.example.android.weatherme.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.weatherme.data.network.models.current.toEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest {

    private lateinit var database: WeatherDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            WeatherDatabase::class.java,
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCurrentAndKey() = runBlocking {
        val current = createCurrent().toEntity()
        val key = database.currentWeatherDao().insertCurrent(current)
        val retrieved = database.currentWeatherDao().getCurrentByKey(key)
        assertThat(retrieved.value, `is`(notNullValue()))
        assertThat(retrieved.value, `is`(current.cityName))
    }

    @Test
    fun insertCurrentAndRetrieveByName() = runBlocking {
        val current = createCurrent().toEntity()
        database.currentWeatherDao().insertCurrent(current)
        val retrieved = database.currentWeatherDao().getCurrentByName(current.cityName)
        assertThat(retrieved.value?.cityName, `is`(current.cityName))
    }

    @Test
    fun insertCurrentsAndRetrieveAll() = runBlocking {
        val current = createCurrent().toEntity()
        database.currentWeatherDao().insertCurrent(current)
        val alternative = createCurrent().toEntity()
        database.currentWeatherDao().insertCurrent(alternative)
        val currents = database.currentWeatherDao().getCurrents()
        assertThat(currents.value, `is`(2))
    }
}