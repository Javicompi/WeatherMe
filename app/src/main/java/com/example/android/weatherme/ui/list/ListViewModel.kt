package com.example.android.weatherme.ui.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.Result
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.getDatabase
import com.example.android.weatherme.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ListViewModel(app: Application) : BaseViewModel(app) {

    private val database = getDatabase(app)

    private val repository = Repository(database)

    val currentList = MutableLiveData<List<CurrentEntity>>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is list Fragment"
    }
    val text: LiveData<String> = _text

    init {
        loadCurrentList()
    }

    fun loadCurrentList() {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getCurrents()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<List<CurrentEntity>> -> {
                    currentList.value = result.data
                }
                is Result.Error -> {
                    showSnackBar.value = result.message
                }
            }
            invalidateShowNoData()
        }
    }

    private fun invalidateShowNoData() {
        showNoData.value = currentList.value == null || currentList.value!!.isEmpty()
    }
}