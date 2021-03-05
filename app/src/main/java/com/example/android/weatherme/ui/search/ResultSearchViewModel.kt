package com.example.android.weatherme.ui.search

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourEntity
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.data.network.models.perhour.toHourlyEntityList
import com.example.android.weatherme.data.network.models.perhour.toPerHourEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ResultSearchViewModel @ViewModelInject constructor(
    app: Application,
    val repository: Repository
) : AndroidViewModel(app) {

    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val navigateToCurrentFragment: SingleLiveEvent<Long> = SingleLiveEvent()

    private val _current: MutableLiveData<CurrentEntity> = MutableLiveData()
    val current: LiveData<CurrentEntity>
        get() = _current

    private val _perHour: MutableLiveData<PerHourWithHourly> = MutableLiveData()
    val perHour: LiveData<PerHourWithHourly>
        get() = _perHour

    fun setCurrent(current: CurrentEntity) {
        _current.postValue(current)
    }

    fun searchPerHour(current: CurrentEntity) {
        viewModelScope.launch {
            val result = repository.searchPerHourByLatLon(current.latitude, current.longitude)
            when(result) {
                is Result.Success -> {
                    val perHourEntity = result.value.toPerHourEntity(current.cityId)
                    val hourlyEntity = result.value.toHourlyEntityList(current.cityId)
                    val perHourAndHourlys = PerHourWithHourly(perHourEntity, hourlyEntity)
                    _perHour.value = perHourAndHourlys
                }
            }
        }
    }

    fun saveCurrent() {
        viewModelScope.launch {
            val id = _current.value?.let { repository.saveCurrent(it) }
            if (id != null && id > 0) {
                navigateToCurrentFragment.postValue(id)
            } else {
                showSnackBarInt.postValue(R.string.error_saving)
            }
        }
    }
}