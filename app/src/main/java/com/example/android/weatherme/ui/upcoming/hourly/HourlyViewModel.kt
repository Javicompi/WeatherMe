package com.example.android.weatherme.ui.upcoming.hourly

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.hourly.HourlyEntity
import kotlinx.coroutines.launch

class HourlyViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val hourlyList = MediatorLiveData<List<HourlyEntity>>()

    val showData = hourlyList.map { hourlys ->
        hourlys.isNotEmpty()
    }

    fun loadHourlys() {
        showLoading.value = true
        viewModelScope.launch {
            hourlyList.addSource(repository.getHourlys()) {
                hourlyList.value = it
            }
            showLoading.postValue(false)
        }
    }
}