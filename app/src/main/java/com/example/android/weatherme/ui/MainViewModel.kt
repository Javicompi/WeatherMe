package com.example.android.weatherme.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.Result
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.network.models.current.Current
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import com.example.android.weatherme.utils.isInternetAvailable
import com.example.android.weatherme.utils.notifyObserver
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application) : ViewModel() {

    private val TAG = MainViewModel::class.java.simpleName

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()

    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    private val repository: Repository

    val listFragmentViewState: MutableLiveData<ViewState> = MutableLiveData()

    val currentFragmentViewState: MutableLiveData<ViewState> = MutableLiveData()

    val currentList = MutableLiveData<List<CurrentEntity>>()

    val currentSelected = MutableLiveData<CurrentEntity>()

    val setShowSaveFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        Log.d(TAG, "currentSelected changed: ${it.key}")
        it.key == 0L //&& !currentExists()
    }

    val setShowClearFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        it.key > 0L //&& currentExists()
    }

    init {
        Log.d(TAG, "init")
        listFragmentViewState.value = ViewState()
        currentFragmentViewState.value = ViewState()
        repository = Repository.getRepository(app)
        loadCurrentList()
        loadCurrentSelected()
        Log.d(TAG, "showFab: ${setShowSaveFab.value}")
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
                        currentList.value = result.data
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

    private fun loadCurrentSelected(key: Long = 0) {
        Log.d(TAG, "loadCurrentSelected")
        setCurrentViewState(State.LOADING)
        viewModelScope.launch {
            val result = repository.getCurrentByKey(key)
            when (result) {
                is Result.Success<CurrentEntity> -> {
                    Log.d(TAG, "loadCurrentSelected Success")
                    currentSelected.value = result.data
                    setCurrentViewState(State.LOADED)
                }
                is Result.Error -> {
                    Log.d(TAG, "loadCurrentSelected Error")
                    setCurrentViewState(State.NODATA)
                }
            }
        }
    }

    fun searchCurrent(name: String?, lat: Double?, lon: Double?) {
        Log.d(TAG, "searchByName")
        if (isInternetAvailable(app)) {
            setCurrentViewState(State.LOADING)
            viewModelScope.launch {
                try {
                    val result: Current
                    if (name?.isNotEmpty() == true) {
                        result = repository.searchCurrentByName(name)
                    } else {
                        result = repository.searchCurrentByLatLon(lat!!, lon!!)
                    }
                    Log.d(TAG, "result: $result")
                    setCurrentViewState(State.LOADED)
                    currentSelected.value = result.toEntity()
                } catch (exception: Exception) {
                    Log.e(TAG, exception.message.toString())
                    setCurrentViewState(State.NODATA)
                    showSnackBar.value = exception.localizedMessage
                }
            }
        } else {
            setCurrentViewState(State.NODATA)
            showSnackBarInt.value = R.string.no_internet_connection
        }
    }

    fun saveCurrent() {
        if (currentSelected.value != null && currentSelected.value!!.key == 0L) {
            viewModelScope.launch {
                val savedKey = repository.saveCurrent(currentSelected.value!!)
                Log.d(TAG, "savedCurrent id: $savedKey")
                loadCurrentList()
                currentSelected.value!!.key = savedKey
                currentSelected.notifyObserver()
            }
        }
    }

    fun deleteCurrent() {
        viewModelScope.launch {
            repository.deleteCurrent(currentSelected.value?.key!!)
            currentSelected.value!!.key = -1
            currentSelected.notifyObserver()
            loadCurrentList()
            setCurrentViewState(State.NODATA)
        }
    }

    fun onCurrentClicked(currentEntity: CurrentEntity) {
        Log.d(TAG, "onCurrentClicked: ${currentEntity.cityName}")
        loadCurrentSelected(currentEntity.key)
    }

    private fun setListViewState(state: State) {
        listFragmentViewState.value?.setState(state)
        listFragmentViewState.notifyObserver()
    }

    private fun setCurrentViewState(state: State) {
        currentFragmentViewState.value?.setState(state)
        currentFragmentViewState.notifyObserver()
    }
}