package com.example.android.weatherme.ui.current

import android.app.Application
import android.util.Log
import androidx.constraintlayout.widget.ConstraintSet
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

    init {
        Log.d("CurrentViewModel", "test")
    }

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val loadNewCurrent: SingleLiveEvent<Long> = SingleLiveEvent()
    //val loadNewCurrent: MutableLiveData<Long> = MutableLiveData(0L)

    /*val currentSelected: LiveData<CurrentEntity> = savedStateHandle.getLiveData<Long>("cityId").switchMap { cityId ->
        Log.d("CurrentViewModel", "cityId: $cityId")
        repository.getCurrentByKey(cityId)
    }*/
    val currentSelected = loadNewCurrent.switchMap { cityId ->
        repository.getCurrentByKey(cityId)
    }

    val setShowData: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.cityName?.isNotEmpty() ?: false
    }

    val setShowLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun deleteCurrent() {
        viewModelScope.launch {
            currentSelected.value?.let { repository.deleteCurrent(it.cityId) }
        }
    }
}