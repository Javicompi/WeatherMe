package com.example.android.weatherme.ui.current

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherme.ui.base.BaseViewModel

class CurrentViewModel(app: Application) : BaseViewModel(app) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is current Fragment"
    }
    val text: LiveData<String> = _text
}