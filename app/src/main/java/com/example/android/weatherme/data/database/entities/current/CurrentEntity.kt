package com.example.android.weatherme.data.database.entities.current

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currents")
data class CurrentEntity(
        val latitude: Double,
        val longitude: Double,
        val weatherId: Int,
        val shortDescription: String?,
        val description: String?,
        val icon: String?,
        val temp: Int,
        val tempMin: Int,
        val tempMax: Int,
        val feelsLike: Int,
        val pressure: Int,
        val humidity: Int,
        val windSpeed: Int,
        val windDegrees: Int,
        val clouds: Int,
        val rainOneHour: Int = 0,
        val rainThreeHours: Int = 0,
        val snowOneHour: Int = 0,
        val snowThreeHours: Int = 0,
        val country: String?,
        val sunrise: Long,
        val sunset: Long,
        val timeZone: Int,
        @PrimaryKey
        val cityId: Long,
        val cityName: String?,
        val deltaTime: Long,
        val visibility: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeInt(weatherId)
        parcel.writeString(shortDescription)
        parcel.writeString(description)
        parcel.writeString(icon)
        parcel.writeInt(temp)
        parcel.writeInt(tempMin)
        parcel.writeInt(tempMax)
        parcel.writeInt(feelsLike)
        parcel.writeInt(pressure)
        parcel.writeInt(humidity)
        parcel.writeInt(windSpeed)
        parcel.writeInt(windDegrees)
        parcel.writeInt(clouds)
        parcel.writeInt(rainOneHour)
        parcel.writeInt(rainThreeHours)
        parcel.writeInt(snowOneHour)
        parcel.writeInt(snowThreeHours)
        parcel.writeString(country)
        parcel.writeLong(sunrise)
        parcel.writeLong(sunset)
        parcel.writeInt(timeZone)
        parcel.writeLong(cityId)
        parcel.writeString(cityName)
        parcel.writeLong(deltaTime)
        parcel.writeInt(visibility)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentEntity> {
        override fun createFromParcel(parcel: Parcel): CurrentEntity {
            return CurrentEntity(parcel)
        }

        override fun newArray(size: Int): Array<CurrentEntity?> {
            return arrayOfNulls(size)
        }
    }
}