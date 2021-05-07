package com.example.android.weatherme.ui.weather.hourly

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository

class HourlyViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val hourlyList = savedStateHandle.getLiveData<Long>("cityId").switchMap { cityId ->
        repository.getHourlys(cityId)
    }

    val showData = hourlyList.map { hourlys ->
        hourlys.isNotEmpty()
    }

    fun loadHourlys(cityId: Long) {
        savedStateHandle["cityId"] = cityId
    }
}