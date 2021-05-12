package com.example.android.weatherme.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.CurrentEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ResultSearchViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val navigateToCurrentFragment: SingleLiveEvent<Long> = SingleLiveEvent()

    /*private val repository = Repository(
        WeatherDatabase.getDatabase(app),
        PreferenceManager.getDefaultSharedPreferences(app)
    )*/

    private val _current: MutableLiveData<CurrentEntity> = MutableLiveData()
    val current: LiveData<CurrentEntity>
        get() = _current

    fun setCurrent(current: CurrentEntity) {
        _current.postValue(current)
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