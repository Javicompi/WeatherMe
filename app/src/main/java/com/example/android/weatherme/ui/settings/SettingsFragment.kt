package com.example.android.weatherme.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.WeatherDatabase
import com.example.android.weatherme.utils.Constants

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = SettingsFragment::class.java.simpleName

    private lateinit var repository: Repository

    private var automaticUpdate: Boolean = false
    private var automaticUpdateNew = automaticUpdate

    private lateinit var units: String
    private lateinit var unitsNew: String

    private lateinit var autUpdatePreference: SwitchPreferenceCompat
    private lateinit var unitsPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        autUpdatePreference = findPreference(Constants.PREF_AUT_UPDATE)!!
        automaticUpdate = autUpdatePreference.isChecked
        automaticUpdateNew = automaticUpdate
        unitsPreference = findPreference(Constants.PREF_UNITS)!!
        units = unitsPreference.value
        unitsNew = units
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == resources.getString(R.string.pref_automatic_update_key)) {
            Log.d(TAG, "automatic updates changed")
            automaticUpdateNew = sharedPreferences.getBoolean(key, false)
        } else if (key == resources.getString(R.string.pref_units_key)) {
            Log.d(TAG, "units changed")
            unitsNew = sharedPreferences.getString(key, getString(R.string.pref_units_standard)).toString()
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        if (automaticUpdate != automaticUpdateNew) {
            if (automaticUpdateNew) {
                Log.d(TAG, "automatic update changed, launch coroutine worker")
            } else {
                Log.d(TAG, "automatic update changed, cancel coroutine worker")
            }
        }
        if (units != unitsNew) {
            Log.d(TAG, "units changed, launch repository refresh")
            repository = Repository(WeatherDatabase.getDatabase(requireActivity()))
        }
    }
}