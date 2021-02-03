package com.example.android.weatherme.ui

enum class State { LOADING, LOADED, NODATA}

data class ViewState(
        var showLoading: Boolean = false,
        var showNoData: Boolean = false,
        var showData: Boolean = false
) {
    fun setState(value: State) {
        when(value) {
            State.LOADING -> {
                showLoading = true
                showNoData = false
                showData = false
            }
            State.NODATA -> {
                showLoading = false
                showNoData = true
                showData = false
            }
            State.LOADED -> {
                showLoading = false
                showNoData = false
                showData = true
            }
        }
    }

    fun isLoading() = showLoading

    fun isLoaded() =showData
}