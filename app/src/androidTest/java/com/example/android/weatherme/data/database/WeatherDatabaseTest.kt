package com.example.android.weatherme.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.weatherme.data.network.models.current.toEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
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
        val key = database.currentDao().insertCurrent(current)
        val retrieved = database.currentDao().getCurrentByKey(key)
        assertThat(retrieved.value, `is`(notNullValue()))
        assertThat(retrieved.value, `is`(current.cityName))
    }

    @Test
    fun insertCurrentAndRetrieveByName() = runBlocking {
        val current = createCurrent().toEntity()
        database.currentDao().insertCurrent(current)
        val retrieved = database.currentDao().getCurrentByName(current.cityName!!)
        assertThat(retrieved.value?.cityName, `is`(current.cityName))
    }

    @Test
    fun insertCurrentsAndRetrieveAll() = runBlocking {
        val current = createCurrent().toEntity()
        database.currentDao().insertCurrent(current)
        val alternative = createCurrent().toEntity()
        database.currentDao().insertCurrent(alternative)
        val currents = database.currentDao().getCurrents()
        assertThat(currents.value, `is`(2))
    }

    @Test
    fun retrieveIds() = runBlocking {
        val current = createCurrent().toEntity()
        database.currentDao().insertCurrent(current)
        val ids = database.currentDao().getCityIds()
        assertThat(ids, not(emptyList()))
        assertThat(ids.size, `is`(1))
    }
}