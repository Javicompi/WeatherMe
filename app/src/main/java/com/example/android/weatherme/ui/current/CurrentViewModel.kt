package com.example.android.weatherme.ui.current

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CurrentViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val currentSelected: LiveData<CurrentEntity> =
        savedStateHandle.getLiveData<Long>("cityId").switchMap { cityId ->
            repository.getCurrentByKey(cityId)
    }

    val setShowData: LiveData<Boolean> = currentSelected.map { current ->
        current.cityName?.isNotEmpty() ?: false
    }

    val setShowLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun loadCurrent(cityId: Long) {
        savedStateHandle["cityId"] = cityId
    }

    fun deleteCurrent() {
        viewModelScope.launch {
            currentSelected.value?.let {
                repository.deleteCurrent(it.cityId)
                savedStateHandle["cityId"] = 0L
            }
        }
    }
}