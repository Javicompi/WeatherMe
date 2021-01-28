package com.example.android.weatherme.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherme.utils.SingleLiveEvent

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()

    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val showNoData: MutableLiveData<Boolean> = MutableLiveData()
}