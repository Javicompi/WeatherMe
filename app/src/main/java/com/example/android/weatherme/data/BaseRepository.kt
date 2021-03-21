package com.example.android.weatherme.data

import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.models.ErrorResponse
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

open class BaseRepository {

    suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): Result<T> {
        return withContext(dispatcher) {
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> Result.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val message = throwable.message()
                        //val errorResponse = convertErrorBody(throwable)
                        val errorResponse = ErrorResponse(code, message)
                        Result.GenericError(code, errorResponse)
                    }
                    is JsonDataException -> {
                        val code = 0
                        val message = throwable.message ?: "Error parsing the data"
                        //val errorResponse = convertJsonException(throwable)
                        val errorResponse = ErrorResponse(code, message)
                        Result.GenericError(0, errorResponse)
                    }
                    else -> {
                        Result.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun convertJsonException(throwable: JsonDataException): ErrorResponse? {
        return try {
                ErrorResponse(0, throwable.localizedMessage ?: "Error parsing the data")
            } catch (exception: Exception) {
                return null
        }
    }
}