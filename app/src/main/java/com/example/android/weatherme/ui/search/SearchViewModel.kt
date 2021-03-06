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

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val current: SingleLiveEvent<Current> = SingleLiveEvent()

    fun searchLocation(name: String? = null, location: Location? = null) {
        showLoading.postValue(true)
        viewModelScope.launch {
            val result = if (!name.isNullOrEmpty()) {
                repository.searchCurrentByName(name)
            } else {
                repository.searchCurrentByLocation(location!!)
            }
            when (result) {
                is Result.Success -> {
                    current.postValue(result.value)
                }
                is Result.GenericError -> {
                    val errorMessage = result.error?.message ?: R.string.unknown_error
                    showSnackBar.postValue(errorMessage.toString().capitalize(Locale.getDefault()))
                }
                is Result.NetworkError -> {
                    val errorMessage = result.message ?: R.string.connectivity_error
                    showSnackBar.postValue(errorMessage.toString().capitalize(Locale.getDefault()))
                }
            }
            showLoading.postValue(false)
        }
    }
}