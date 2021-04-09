package com.example.android.weatherme.ui.search

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    /*private val repository = Repository(
        WeatherDatabase.getDatabase(app),
        PreferenceManager.getDefaultSharedPreferences(app)
    )*/

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val current: SingleLiveEvent<Current> = SingleLiveEvent()

    fun searchByName(name: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            when (val result = repository.searchCurrentByName(name)) {
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