package com.example.android.weatherme.ui.weather.daily

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.weatherme.data.Repository

class DailyViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dailyList = savedStateHandle.getLiveData<Long>("cityId").switchMap { cityId ->
        repository.getDailys(cityId)
    }

    val showData = dailyList.map { dailys ->
        dailys.isNotEmpty() && dailys.size > 1
    }

    fun loadDailys(cityId: Long) {
        savedStateHandle["cityId"] = cityId
    }
}