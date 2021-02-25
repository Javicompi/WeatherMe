package com.example.android.weatherme.utils

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
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

    fun getLastUpdate(): Long {
        return prefs.getLong(Constants.PREF_LAST_UPDATE, 0)
    }

    fun setLastUpdate(time: Long) {
        prefs.edit().putLong(Constants.PREF_LAST_UPDATE, time).apply()
    }

    fun getAutUpdate(): Boolean {
        return prefs.getBoolean(Constants.PREF_AUT_UPDATE, false)
    }

    fun shouldUpdateCurrents(): Boolean {
        val updateDelay = TimeUnit.MINUTES.toMillis(Constants.PERIODIC_REQUEST_DELAY_MINS)
        return !getAutUpdate() && System.currentTimeMillis() - getLastUpdate() > updateDelay
    }
}