package com.example.android.weatherme.utils

import com.example.android.weatherme.BuildConfig

object Constants {

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    const val API_KEY = BuildConfig.API_KEY

    const val REQUEST_LOCATION_PERMISSION_CODE = 33

    const val LOCATION_REQUEST_INTERVAL = 2000L

    const val LOCATION_REQUEST_FASTEST_INTERVAL = 2000L

    const val PREF_AUT_UPDATE = "prefAutomaticUpdateKey"

    const val PREF_UNITS = "prefUnitsKey"

    const val PREF_UNITS_DEFAULT = "standard"

    const val PREF_LAST_UPDATE = "prefLastUpdate"

    //TODO for testing, only 15 minutes
    const val PERIODIC_REQUEST_DELAY_MINS = 15L

    const val CURRENT_SELECTED = "prefCurrentSelected"
}
