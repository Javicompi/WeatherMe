package com.example.android.weatherme.ui.search

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ResultSearchViewModel(app: Application) : AndroidViewModel(app) {

    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val navigateToCurrentFragment: SingleLiveEvent<Long> = SingleLiveEvent()

    private val repository = Repository(
        WeatherDatabase.getDatabase(app),
        PreferenceManager.getDefaultSharedPreferences(app)
    )

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