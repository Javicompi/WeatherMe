package com.example.android.weatherme.utils

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getUnits(): String {
        return prefs.getString(Constants.PREF_UNITS, "standard")!!
    }

    fun setUnits(units: String) {
        prefs.edit().putString(Constants.PREF_UNITS, units).apply()
    }
}