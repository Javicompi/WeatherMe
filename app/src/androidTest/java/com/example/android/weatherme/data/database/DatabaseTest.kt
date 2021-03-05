package com.example.android.weatherme.data.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.data.network.models.perhour.toHourlyEntityList
import com.example.android.weatherme.data.network.models.perhour.toPerHourEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val dispatcher = TestCoroutineDispatcher()
    val testScope = TestCoroutineScope(dispatcher)

    private lateinit var db: WeatherDatabase

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
                .setTransactionExecutor(dispatcher.asExecutor())
                .setQueryExecutor(dispatcher.asExecutor())
                .build()
    }

    @After
    fun closeDB() {
        db.close()
    }

    @Test
    fun saveCurrentRetrieveCurrent() = testScope.runBlockingTest {
        val result = createCurrent()
        db.currentWeatherDao().insertCurrent(result.toEntity())
        val retrieved = db.currentWeatherDao().getCurrentByName(result.name).getOrAwaitValue()
        assertThat(retrieved.cityName, `is`("Gran Alacant"))
    }

    @Test
    fun saveCurrentsRetrieveCurrents() = testScope.runBlockingTest {
        val resultFirst = createCurrent()
        val resultSecond = createCurrent("Elche", 6697299)
        db.currentWeatherDao().insertCurrent(resultFirst.toEntity())
        db.currentWeatherDao().insertCurrent(resultSecond.toEntity())
        val currents = db.currentWeatherDao().getCurrents().getOrAwaitValue()
        assertThat(currents.size, `is`(2))
        assertThat(currents.find { it.cityName == "Gran Alacant" }, `is`(notNullValue()))
        assertThat(currents.find { it.cityName == "Elche" }, `is`(notNullValue()))
    }

    @Test
    fun savePerHourRetrievePerHour() = testScope.runBlockingTest {
        val result = createPerHour()
        db.perHourWeatherDao().insertPerHour(result.toPerHourEntity(6697298))
        db.perHourWeatherDao().insertHourlys(result.toHourlyEntityList(6697298))
        val perHour = db.perHourWeatherDao().getPerHourbyKey(6697298).getOrAwaitValue()
        assertThat(perHour.perHourEntity.cityId, `is`(6697298))
        assertThat(perHour.hourlyEntities.size, `is`(48))
    }
}