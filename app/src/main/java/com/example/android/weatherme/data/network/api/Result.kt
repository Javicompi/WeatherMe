package com.example.android.weatherme.data.network.api

sealed class Result<out T> {

    data class Success<out T>(val value: T): Result<T>()
    data class GenericError(val code: Int? = null, val error: String? = null): Result<Nothing>()
    object NetworkError: Result<Nothing>()
}