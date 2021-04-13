package com.example.android.weatherme.data.network.api

import com.example.android.weatherme.data.network.models.ErrorResponse
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.perhour.PerHour
import com.example.android.weatherme.utils.Constants
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .baseUrl(Constants.BASE_URL)
        .build()

interface WeatherApiService {

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentResponseById(
            @Query("id") id: Long,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().language
    ): NetworkResponse<Current, ErrorResponse>

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentResponseByName(
            @Query("q") location: String,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().language
    ): NetworkResponse<Current, ErrorResponse>

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getCurrentResponseByLatLon(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String? = "metric",
            @Query("lang") language: String = Locale.getDefault().language
    ): NetworkResponse<Current, ErrorResponse>

    @GET("onecall?appid=${Constants.API_KEY}")
    suspend fun getPerHourByLatLon(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("units") units: String? = Constants.PREF_UNITS_DEFAULT,
            @Query("exclude") exclude: String = "current,minutely,daily,alerts",
            @Query("lang") language: String = Locale.getDefault().language
    ): NetworkResponse<PerHour, ErrorResponse>
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}