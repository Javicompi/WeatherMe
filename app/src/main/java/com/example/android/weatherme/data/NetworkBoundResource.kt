package com.example.android.weatherme.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.android.weatherme.data.database.entities.perhour.ApiEmptyResponse
import com.example.android.weatherme.data.database.entities.perhour.ApiErrorResponse
import com.example.android.weatherme.data.database.entities.perhour.ApiResponse
import com.example.android.weatherme.data.database.entities.perhour.ApiSuccessResponse
import kotlinx.coroutines.*

abstract class NetworkBoundResource<ResultType, RequestType>
constructor(dispatcher: CoroutineDispatcher){

    private val result = MediatorLiveData<Resource<ResultType>>()

    private val job = Job()

    init {
        dispatcher + job
        CoroutineScope(dispatcher).launch {
            result.postValue(Resource.loading(null))
            val dbSource = loadFromDb()
            result.addSource(dbSource) { data ->
                result.removeSource(dbSource)
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource)
                } else {
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    saveCallResult(processResponse(response))
                    result.addSource(loadFromDb()) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
                is ApiErrorResponse -> {

                }
                is ApiEmptyResponse -> {
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
            }
        }
    }

    protected abstract fun saveCallResult(item: RequestType)

    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun loadFromDb(): LiveData<ResultType>

    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>
}