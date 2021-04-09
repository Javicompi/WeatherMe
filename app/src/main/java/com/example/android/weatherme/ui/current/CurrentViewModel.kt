package com.example.android.weatherme.ui.current

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.Resource
import com.example.android.weatherme.data.Status
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.api.Result
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CurrentViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    private val loadNewCurrent: SingleLiveEvent<Long> = SingleLiveEvent()

    /*val currentSelected = loadNewCurrent.switchMap { cityId ->
        liveData {
            emitSource(repository.getCurrentByKey(cityId))
        }
    }*/

    val currentSelected: LiveData<Resource<CurrentEntity>> = loadNewCurrent.switchMap { cityId ->
        repository.loadCurrent(cityId)
    }

    val perHour = loadNewCurrent.switchMap { cityId ->
        liveData {
            emitSource(repository.getPerHourByKey(cityId))
        }
    }

    val setShowData: LiveData<Boolean> = Transformations.map(currentSelected) { value ->
        value.data?.let {
            it.cityName?.isNotEmpty() ?: false
        }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            currentSelected.value?.let {
                if (it.status == Status.SUCCESS) {
                    val cityId = it.data?.cityId ?: 0
                    repository.deleteCurrent(cityId)
                    repository.deletePerHour(cityId)
                }
            }
        }
    }

    fun loadCurrent(cityId: Long) {
        loadNewCurrent.postValue(cityId)
    }
}