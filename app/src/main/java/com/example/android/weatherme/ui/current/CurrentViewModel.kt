package com.example.android.weatherme.ui.current

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CurrentViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    private val loadNewCurrent: SingleLiveEvent<Long> = SingleLiveEvent()

    val currentSelected = loadNewCurrent.switchMap { cityId ->
        liveData {
            emitSource(repository.getCurrentByKey(cityId))
        }
    }

    val perHour = loadNewCurrent.switchMap { cityId ->
        liveData {
            emitSource(repository.getPerHourByKey(cityId))
        }
    }

    val setShowData: LiveData<Boolean> = Transformations.map(currentSelected) {
        it?.let {
            it.cityName?.isNotEmpty() ?: false
        }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            currentSelected.value?.let {
                val cityId = it.cityId
                repository.deleteCurrent(cityId)
                repository.deletePerHour(cityId)
            }
        }
    }

    fun loadCurrent(cityId: Long) {
        loadNewCurrent.postValue(cityId)
    }
}