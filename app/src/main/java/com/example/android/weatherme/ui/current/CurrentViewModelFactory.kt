package com.example.android.weatherme.ui.current

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.weatherme.ui.list.ListViewModel

class CurrentViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return null as T//CurrentViewModel() as T
        }
        throw IllegalArgumentException("Unable to construct CurrentViewModel")
    }
}