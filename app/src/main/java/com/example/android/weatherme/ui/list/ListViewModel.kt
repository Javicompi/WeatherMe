package com.example.android.weatherme.ui.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.Result
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.ui.State
import com.example.android.weatherme.ui.ViewState
import com.example.android.weatherme.utils.SingleLiveEvent
import com.example.android.weatherme.utils.notifyObserver
import kotlinx.coroutines.launch

class ListViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = ListViewModel::class.java.simpleName

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val listFragmentViewState: MutableLiveData<ViewState> = MutableLiveData()

    private val _currentList = MutableLiveData<List<CurrentEntity>>()
    val currentList: LiveData<List<CurrentEntity>>
        get() = _currentList

    private val _navigateToCurrent = SingleLiveEvent<Long>()
    val navigateToCurrent: LiveData<Long>
        get() = _navigateToCurrent

    private val repository: Repository

    init {
        Log.d(TAG, "init")
        listFragmentViewState.value = ViewState()
        repository = Repository.getRepository(app)
        loadCurrentList()
    }

    private fun loadCurrentList() {
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
    }

    fun onCurrentClicked(currentEntity: CurrentEntity) {
        Log.d(TAG, "onCurrentClicked: ${currentEntity.cityName}")
        _navigateToCurrent.postValue(currentEntity.key)
    }

    private fun setListViewState(state: State) {
        listFragmentViewState.value?.setState(state)
        listFragmentViewState.notifyObserver()
    }
}