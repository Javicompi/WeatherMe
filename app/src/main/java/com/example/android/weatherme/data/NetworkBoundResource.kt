package com.example.android.weatherme.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.android.weatherme.data.network.models.ErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class NetworkBoundResource<ResultType, RequestType : Any>(dispatcher: CoroutineDispatcher) {

    private val result = MediatorLiveData<ResultType>()

    private val job = Job()

    init {
        val context = dispatcher + job
        CoroutineScope(context).launch {
            val dbSource = loadFromDb()
            result.addSource(dbSource) { data ->
                result.removeSource(dbSource)
                if (shouldUpdate(data)) {
                    updateData(data)
                } else {
                    result.postValue(data)
                }
            }
        }
    }

    private fun updateData(data: ResultType) {
        val response = makeApiCall()
        if (response is NetworkResponse.Success) {
            saveResult(response.body)
        }
    }

    protected abstract fun saveResult(item: RequestType)

    protected abstract fun shouldUpdate(data: ResultType?): Boolean

    protected abstract suspend fun loadFromDb(): LiveData<ResultType>

    protected abstract fun makeApiCall(): NetworkResponse<RequestType, ErrorResponse>

    fun asLiveData() = result as LiveData<ResultType>
}