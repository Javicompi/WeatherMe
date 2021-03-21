package com.example.android.weatherme.ui.search

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
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
    val showSnackBarMessage: SingleLiveEvent<String> = SingleLiveEvent()
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
                    _perHour.postValue(perHourAndHourlys)
                }
                is Result.GenericError -> {
                    showSnackBarMessage.postValue(result.error?.message)
                }
                is Result.NetworkError -> {
                    showSnackBarInt.postValue(R.string.connectivity_error)
                }
            }
        }
    }

    fun saveData() {
        viewModelScope.launch {
            val currentId = _current.value?.let { repository.saveCurrent(it) }
            if (currentId != null && currentId > 0) {
                _perHour.value?.let {
                    val newPerHourWithHourly = PerHourWithHourly(
                            it.perHourEntity,
                            it.hourlyEntities
                    )
                    repository.savePerHourWithHourly(newPerHourWithHourly)
                }
                navigateToCurrentFragment.postValue(currentId)
            } else {
                showSnackBarInt.postValue(R.string.error_saving)
            }
        }
    }
}