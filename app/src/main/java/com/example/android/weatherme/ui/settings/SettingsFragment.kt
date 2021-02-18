package com.example.android.weatherme.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.android.weatherme.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = SettingsFragment::class.java.simpleName

    private var automaticUpdate: Boolean = false
    private var automaticUpdateNew = automaticUpdate
    private lateinit var units: String
    private lateinit var unitsNew: String
    private lateinit var autUpdatePreference: SwitchPreferenceCompat
    private lateinit var unitsPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        autUpdatePreference = findPreference(getString(R.string.pref_automatic_update_key))!!
        automaticUpdate = autUpdatePreference.isChecked
        unitsPreference = findPreference(getString(R.string.pref_units_key))!!
        units = unitsPreference.value
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
}