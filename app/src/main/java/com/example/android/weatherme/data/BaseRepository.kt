package com.example.android.weatherme.data

import android.util.Log
import com.example.android.weatherme.data.network.api.Result
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

open class BaseRepository {

    suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): Result<T> {
        return withContext(dispatcher) {
            Log.d("safeApiCall", apiCall.invoke().toString())
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> Result.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val message = throwable.message()
                        Result.GenericError(code, message)
                    }
                    is JsonDataException -> {
                        val code = 0
                        val message = throwable.message ?: "Error parsing the data"
                        Result.GenericError(0, message)
                    }
                    else -> {
                        val code = 0
                        val message = throwable.message
                        Result.GenericError(code, message)
                    }
                }
            }
        }
    }
}