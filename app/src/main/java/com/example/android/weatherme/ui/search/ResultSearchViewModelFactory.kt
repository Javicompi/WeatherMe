package com.example.android.weatherme.ui.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResultSearchViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResultSearchViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct ResultSearchViewModel")
    }
}