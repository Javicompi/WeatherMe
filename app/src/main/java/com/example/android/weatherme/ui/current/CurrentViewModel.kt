package com.example.android.weatherme.ui.current

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.utils.SingleLiveEvent
import com.example.android.weatherme.utils.notifyObserver
import kotlinx.coroutines.launch

class CurrentViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = CurrentViewModel::class.java.simpleName

    private val repository: Repository

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()

    val currentSelected = MediatorLiveData<CurrentEntity>()

    val setShowData: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.cityName.isNotEmpty()
    }

    val setShowLoading: MutableLiveData<Boolean> = MutableLiveData()

    val setShowSaveFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.key == 0L
    }

    val setShowClearFab: LiveData<Boolean> = Transformations.map(currentSelected) {
        it != null && it.key > 0L
    }

    init {
        repository = Repository.getRepository(app)
    }

    fun loadCurrent(value: Long) {
        Log.d(TAG, "loadCurrent: $value")
        setShowLoading.postValue(true)
        currentSelected.value?.key = -1
        currentSelected.notifyObserver()
        viewModelScope.launch {
            currentSelected.addSource(repository.getCurrentByKey(value)) {
                currentSelected.value = it

            }
            setShowLoading.postValue(false)
        }
    }

    fun searchCurrentByName(value: String) {
        Log.d(TAG, "searchCurrentByName: $value")
        setShowLoading.postValue(true)
        currentSelected.value?.key = -1
        currentSelected.notifyObserver()
        viewModelScope.launch {
            currentSelected.addSource(getEntityByName(value)) {
                currentSelected.postValue(it)
                setShowLoading.postValue(false)
            }
        }
    }

    private fun getEntityByName(name: String) = liveData {
        val result = repository.searchCurrentByName(name)
        emit(result)
    }

    fun searchCurrentByLocation(location: Location) {
        Log.d(TAG, "searchCurrentByLocation: $location")
        setShowLoading.postValue(true)
        currentSelected.value?.key = -1
        currentSelected.notifyObserver()
        viewModelScope.launch {
            currentSelected.addSource(getEntityByLocation(location)) {
                currentSelected.postValue(it)
                Log.d(TAG, currentSelected.value?.cityName.toString())
                setShowLoading.postValue(false)
            }
        }
    }

    private fun getEntityByLocation(location: Location) = liveData {
        val result = repository.searchCurrentByLatLon(location)
        emit(result)
    }

    fun saveCurrent() {
        if (currentSelected.value != null && currentSelected.value?.key == 0L) {
            viewModelScope.launch {

                /*if (repository.currentExists(currentSelected.value.cityName)) {
                    currentSelected.value!!.key = saved.value!!.key
                    showSnackBarInt.value = R.string.already_saved
                } else {
                    val savedKey = repository.saveCurrent(currentSelected.value!!)
                    currentSelected.value!!.key = savedKey
                }*/
                currentSelected.notifyObserver()
            }
        }
    }

    fun deleteCurrent() {
        if (currentSelected.value != null && currentSelected.value!!.key > 0) {
            viewModelScope.launch {
                repository.deleteCurrent(currentSelected.value?.key!!)
                currentSelected.postValue(null)
            }
        }
    }
}