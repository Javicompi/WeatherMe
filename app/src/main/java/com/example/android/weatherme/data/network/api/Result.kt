package com.example.android.weatherme.data.network.api

import com.example.android.weatherme.data.network.models.ErrorResponse

sealed class Result<out T> {

    data class Success<out T>(val value: T): Result<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null): Result<Nothing>()
    //data class GenericError(val errorResponse: ErrorResponse? = null): Result<Nothing>()
    object NetworkError: Result<Nothing>()
    object Loading: Result<Nothing>()
}