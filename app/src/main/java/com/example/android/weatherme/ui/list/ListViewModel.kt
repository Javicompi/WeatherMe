package com.example.android.weatherme.ui.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.android.weatherme.data.Repository

class ListViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val currentList = liveData {
        emitSource(repository.getCurrents())
    }

    val showData = Transformations.map(currentList) {
        it.isNotEmpty()
    }
}