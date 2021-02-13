package com.example.android.weatherme.ui.search

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = SearchViewModel::class.java.simpleName

    private val repository: Repository

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val current: SingleLiveEvent<Current> = SingleLiveEvent()

    init {
        repository = Repository.getRepository(app)
    }

    fun searchByName(name: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val result = repository.searchCurrentByName(name)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "searchByName: ${result.value.name}")
                    current.postValue(result.value)
                }
                is Result.GenericError -> {
                    val errorMessage = result.error?.message?.capitalize(Locale.getDefault())
                    showSnackBar.postValue(errorMessage)
                }
                is Result.NetworkError -> {
                    showSnackBarInt.postValue(R.string.connectivity_error)
                }
            }
            showLoading.postValue(false)
        }
    }

    fun searchByLocation(location: Location) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val result = repository.searchCurrentByLatLon(location)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "searchByLocation: ${result.value.name}")
                    current.postValue(result.value)
                }
                is Result.GenericError -> {
                    val errorMessage = result.error?.message?.capitalize(Locale.getDefault())
                    showSnackBar.postValue(errorMessage)
                }
                is Result.NetworkError -> {
                    showSnackBarInt.postValue(R.string.connectivity_error)
                }
            }
            showLoading.postValue(false)
        }
    }
}