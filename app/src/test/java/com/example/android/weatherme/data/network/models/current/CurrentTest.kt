package com.example.android.weatherme.data.network.models.current

import com.example.android.weatherme.data.createCurrent
import com.example.android.weatherme.data.database.entities.CurrentEntity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CurrentTest {

    @Test
    fun currentModelToEntity() {
        //Create the model
        val currentModel = createCurrent()
        //Check is the right type
        assertThat(currentModel, isA(Current::class.java))
        //Check values are correct
        assertThat(currentModel.name, `is`("Gran Alacant"))
        assertThat(currentModel.id, `is`(6697298))
        //Convert model to Entity
        val entity = currentModel.toEntity()
        //Check is the right type
        assertThat(entity, isA(CurrentEntity::class.java))
        //Check values are correct
        assertThat(entity.cityName, `is`("Gran Alacant"))
        assertThat(entity.cityId, `is`(6697298))
        //Check optional values are correct too
        assertThat(entity.rainOneHour, `is`(0))
        assertThat(entity.snowThreeHours, `is`(0))
    }
}