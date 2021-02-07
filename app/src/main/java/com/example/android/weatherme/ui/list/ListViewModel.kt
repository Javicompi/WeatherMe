package com.example.android.weatherme.ui.list

import android.app.Application
import android.content.ClipData.Item
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.ui.State
import com.example.android.weatherme.ui.ViewState
import com.example.android.weatherme.utils.SingleLiveEvent
import com.example.android.weatherme.utils.notifyObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = ListViewModel::class.java.simpleName

    private val repository: Repository

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    /*private val _currentList: MutableLiveData<List<CurrentEntity>> = MutableLiveData()
    val currentList: LiveData<List<CurrentEntity>>
        get() = _currentList*/
    val currentList = MediatorLiveData<List<CurrentEntity>>()

    val showData = Transformations.map(currentList) {
        it.size > 0
    }

    val showLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        Log.d(TAG, "init")
        repository = Repository.getRepository(app)
        loadCurrentList()
    }

    private fun loadCurrentList() {
        Log.d(TAG, "loadCurrentList")
        showLoading.postValue(true)
        viewModelScope.launch {
            currentList.addSource(repository.getCurrents()) {
                currentList.value = it
            }
            showLoading.postValue(false)
        }
    }

    /*private fun loadCurrentList() {
        Log.d(TAG, "loadCurrentList")
        setListViewState(State.LOADING)
        viewModelScope.launch {
            val result = repository.getCurrents()
            when (result) {
                is Result.Success<List<CurrentEntity>> -> {
                    Log.d(TAG, "loadCurrentList Success")
                    if (result.data.isEmpty()) {
                        Log.d(TAG, "loadCurrentList is empty")
                        setListViewState(State.NODATA)
                    } else {
                        Log.d(TAG, "currentList size is: ${result.data.size}")
                        _currentList.postValue(result.data)
                        setListViewState(State.LOADED)
                    }
                }
                is Result.Error -> {
                    Log.d(TAG, "loadCurrentList Error")
                    setListViewState(State.NODATA)
                    showSnackBar.value = result.message
                }
            }
        }
    }*/
}