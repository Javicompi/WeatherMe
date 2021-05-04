package com.example.android.weatherme.data

import android.util.Log
import androidx.lifecycle.liveData
import com.haroldadmin.cnradapter.NetworkResponse

abstract class NetworkBoundResource<ResultType, RequestType> {

    private val TAG = "NetworkBoundResource"

    fun asLiveData() = liveData {
        val dbValue = loadFromDb()
        emit(dbValue)
        if (shouldFetch(dbValue)) {
            when (val response = createCall(dbValue)) {
                is NetworkResponse.Success<*> -> {
                    Log.d(TAG, "NetworkResponse.Success")
                    saveResult(processResponse(response))
                    emit(loadFromDb())
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, response.error.message ?: "Unknown error")
                }
                is NetworkResponse.ServerError<*> -> {
                    Log.d(TAG, response.body.toString())
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, response.error.message ?: "Network error")
                }
            }
        }
    }

    protected abstract fun processResponse(response: RequestType): ResultType

    protected abstract suspend fun saveResult(item: ResultType)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun loadFromDb(): ResultType

    protected abstract suspend fun createCall(data: ResultType): RequestType
}