package com.example.android.weatherme.data.network.api

import com.example.android.weatherme.data.network.models.ErrorResponse
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.utils.Constants
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapter
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .baseUrl(Constants.BASE_URL)
        .build()

interface WeatherApiService {

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByName(
            @Query("q") location: String,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherByLatLon(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentWeatherById(
            @Query("id") id: Long,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): Current

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentResponseByName(
            @Query("q") location: String,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().toString().subSequence(0, 2).toString()
    ): NetworkResponse<Current, ErrorResponse>
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}