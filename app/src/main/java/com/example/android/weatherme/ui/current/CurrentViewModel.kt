package com.example.android.weatherme.ui.current

import android.app.Application
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CurrentViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository(WeatherDatabase.getDatabase(app))

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val loadNewCurrent: SingleLiveEvent<Long> = SingleLiveEvent()

    val currentSelected = loadNewCurrent.switchMap { value ->
        repository.getCurrentByKey(value)
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