package com.example.android.weatherme.ui.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.CurrentEntity
import kotlinx.coroutines.launch


class ListViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    val currentList = MediatorLiveData<List<CurrentEntity>>()

    val showData = Transformations.map(currentList) {
        it.isNotEmpty()
    }

    fun loadCurrentList() {
        showLoading.value = true
        viewModelScope.launch {
            currentList.addSource(repository.getCurrents()) {
                currentList.value = it
            }
            showLoading.postValue(false)
        }
    }
}