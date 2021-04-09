package com.example.android.weatherme.data.network.api

import androidx.lifecycle.LiveData
import com.example.android.weatherme.data.database.entities.perhour.ApiResponse
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.perhour.PerHour
import com.example.android.weatherme.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()

interface WeatherApiService {

    @GET("weather?appid=${Constants.API_KEY}")
    fun getCurrentById(
            @Query("id") id: Long,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("lang") language: String = Locale.getDefault().language
    ): LiveData<ApiResponse<Current>>

    @GET("weather?appid=${Constants.API_KEY}")
    fun getNewCurrentById(
            @Query("id") id: Long,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("lang") language: String = Locale.getDefault().language
    ): ApiResponse<Current>

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByName(
            @Query("q") location: String,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("lang") language: String = Locale.getDefault().language
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByLatLon(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("lang") language: String = Locale.getDefault().language
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherById(
            @Query("id") id: Long,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("lang") language: String = Locale.getDefault().language
    ): Current

    @GET("onecall?appid=${Constants.API_KEY}")
    suspend fun getPerHourByLatLon(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("exclude") exclude: String = "current,minutely,daily,alerts",
            @Query("lang") language: String = Locale.getDefault().language
    ): PerHour

    @GET("onecall?appid=${Constants.API_KEY}")
    fun getNewPerHourByLatLon(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
        @Query("exclude") exclude: String = "current,minutely,daily,alerts",
        @Query("lang") language: String = Locale.getDefault().language
    ): LiveData<ApiResponse<PerHour>>
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}