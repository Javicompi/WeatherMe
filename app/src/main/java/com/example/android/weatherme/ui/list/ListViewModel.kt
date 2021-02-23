package com.example.android.weatherme.ui.list

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import kotlinx.coroutines.launch

class ListViewModel @ViewModelInject constructor(
    app: Application,
    val repository: Repository
) : AndroidViewModel(app) {

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val currentList = MediatorLiveData<List<CurrentEntity>>()

    val showData = Transformations.map(currentList) {
        it.isNotEmpty()
    }

    init {
        loadCurrentList()
    }

    private fun loadCurrentList() {
        showLoading.postValue(true)
        viewModelScope.launch {
            currentList.addSource(repository.getCurrents()) {
                currentList.value = it
            }
            showLoading.postValue(false)
        }
    }

    fun updateCurrents() {
        viewModelScope.launch {
            repository.shouldUpdateCurrents()
        }
    }
}