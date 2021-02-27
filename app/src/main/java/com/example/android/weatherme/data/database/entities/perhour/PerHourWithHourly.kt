package com.example.android.weatherme.data.database.entities.perhour

import androidx.room.Embedded
import androidx.room.Relation

data class PerHourWithHourly(
        @Embedded
        val perHourEntity: PerHourEntity,
        @Relation(
                parentColumn = "cityId",
                entityColumn = "cityId"
        )
        val hourlyEntities: List<HourlyEntity>
)