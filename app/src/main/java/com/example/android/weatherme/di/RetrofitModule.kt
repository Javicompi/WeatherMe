package com.example.android.weatherme.di

import com.example.android.weatherme.data.network.api.WeatherApiService
import com.example.android.weatherme.utils.Constants
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(NetworkResponseAdapterFactory())
                .baseUrl(Constants.BASE_URL)
    }

    @Singleton
    @Provides
    fun provideWeatherApiService(retrofit: Retrofit.Builder): WeatherApiService {
        return retrofit.build().create(WeatherApiService::class.java)
    }
}