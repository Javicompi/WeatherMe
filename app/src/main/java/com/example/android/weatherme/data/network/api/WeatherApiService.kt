package com.example.android.weatherme.data.network.api

import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

interface WeatherApiService {

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByName(
        @Query("q") location: String,
        @Query("units") units: String = "standard",
        @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByLatLon(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "standard",
        @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): Current
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}