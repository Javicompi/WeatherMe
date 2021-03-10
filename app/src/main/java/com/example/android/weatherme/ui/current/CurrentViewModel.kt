package com.example.android.weatherme.ui.current

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.utils.SingleLiveEvent
import com.example.android.weatherme.utils.shouldUpdate
import kotlinx.coroutines.launch

class CurrentViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val loadNewCurrent: SingleLiveEvent<Long> = SingleLiveEvent()

    val currentSelected = loadNewCurrent.switchMap { cityId ->
        liveData {
            emitSource(repository.getCurrentByKey(cityId))
        }
    }

    val perHour = currentSelected.switchMap { current ->
        liveData {
            emitSource(repository.getPerHourByKey(current.cityId))
            repository.shouldUpdatePerHour(current)
        }
    }

    val setShowData: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.cityName?.isNotEmpty() ?: false
    }

    val setShowLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun deleteEntry() {
        viewModelScope.launch {
            currentSelected.value?.let { repository.deleteCurrent(it.cityId) }
            perHour.value?.let { repository.deletePerHour(it.perHourEntity.cityId) }
        }
    }
}