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

    val listFragmentViewState: MutableLiveData<ViewState> = MutableLiveData()
    val currentFragmentViewState: MutableLiveData<ViewState> = MutableLiveData()

    val currentList = MutableLiveData<List<CurrentEntity>>()
    val currentSelected = MutableLiveData<CurrentEntity>()

    val setShowSaveFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.key == 0L
    }

    val setShowClearFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.key > 0L
    }

    private val repository: Repository

    init {
        Log.d(TAG, "init")
        listFragmentViewState.value = ViewState()
        currentFragmentViewState.value = ViewState()
        repository = Repository.getRepository(app)
        loadCurrentList()
        setCurrentViewState(State.NODATA)
        Log.d(TAG, "currentSelected: ${currentSelected.value}")
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

    private fun loadCurrentSelected(key: Long) {
        Log.d(TAG, "loadCurrentSelected")
        setCurrentViewState(State.LOADING)
        viewModelScope.launch {
            val result = repository.getCurrentByKey(key)
            when (result) {
                is Result.Success<CurrentEntity> -> {
                    Log.d(TAG, "loadCurrentSelected Success")
                    setCurrentViewState(State.LOADED)
                    currentSelected.value = result.data
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
            currentSelected.value?.key = -1
            currentSelected.notifyObserver()
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
                    currentSelected.postValue(result.toEntity())
                } catch (exception: Exception) {
                    Log.e(TAG, exception.message.toString())
                    setCurrentViewState(State.NODATA)
                    //currentSelected.value?.key = 0
                    showSnackBar.value = exception.localizedMessage
                }
            }
        } else {
            setCurrentViewState(State.NODATA)
            showSnackBarInt.value = R.string.no_internet_connection
        }
    }

    fun saveCurrent() {
        if (currentSelected.value != null && currentSelected.value?.key == 0L) {
            viewModelScope.launch {
                val savedKey = repository.saveCurrent(currentSelected.value!!)
                Log.d(TAG, "savedCurrent id: $savedKey")
                loadCurrentList()
                currentSelected.value.let {
                    it?.key = savedKey
                    currentSelected.notifyObserver()
                }
            }
        }
    }

    fun deleteCurrent() {
        if (currentSelected.value != null && currentSelected.value!!.key > 0) {
            viewModelScope.launch {
                repository.deleteCurrent(currentSelected.value?.key!!)
                loadCurrentList()
                setCurrentViewState(State.NODATA)
                currentSelected.value?.key = -1
                currentSelected.notifyObserver()
            }
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

    private fun currentAlreadySaved(id: Int) : Boolean {
         return currentList.value?.find {
            it.cityId == id
        } != null
    }
}