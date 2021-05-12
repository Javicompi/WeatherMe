package com.example.android.weatherme.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class RepositoryTest {

    /*private lateinit var repository: Repository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        repository = Repository.getRepository(ApplicationProvider.getApplicationContext())
    }

    @After
    fun clear() = runBlocking {
        repository.deleteCurrents()
    }

    @Test
    fun saveCurrent_getCurrentByKey() = runBlocking {
        //Create a model
        val current = createCurrent().toEntity()
        //Save to database
        repository.saveCurrent(current)
        //Get entity from database
        val retrieved = repository.getCurrentByKey(current.cityId)
        //Check retrieved is ok
        assertThat(retrieved.value, `is`(true))
        //Cast retrieved as Entity
        retrieved as CurrentEntity
        //Check is the same item
        assertThat(retrieved.cityId, `is`(current.cityId))
    }*/
}