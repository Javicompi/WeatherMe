package com.example.android.weatherme.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var repository: Repository

    private lateinit var units: String
    private lateinit var unitsNew: String

    private lateinit var unitsPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
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
        if (key == resources.getString(R.string.pref_units_key)) {
            unitsNew = sharedPreferences.getString(key, getString(R.string.pref_units_standard)).toString()
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        if (units != unitsNew) {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                launchUpdates()
            }
        }
    }

    private suspend fun launchUpdates() {
        withContext(Dispatchers.IO) {
            repository.updateCurrents()
        }
    }
}