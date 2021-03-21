package com.example.android.weatherme.ui.search

import android.app.Application
import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel @ViewModelInject constructor(
    app: Application,
    val repository: Repository
) : AndroidViewModel(app) {

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val current: SingleLiveEvent<Current> = SingleLiveEvent()

    fun searchByName(name: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val result = repository.searchCurrentByName(name)
            when (result) {
                is Result.Success -> {
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
            when (val result = repository.searchCurrentByLatLon(location)) {
                is Result.Success -> {
                    current.postValue(result.value)
                }
                is Result.GenericError -> {
                    //val error = result.errorResponse?.message?.capitalize(Locale.getDefault())
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