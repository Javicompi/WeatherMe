package com.example.android.weatherme.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.*
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.worker.RefreshDataWorker
import com.example.android.weatherme.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var repository: Repository

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
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == resources.getString(R.string.pref_automatic_update_key)) {
            automaticUpdateNew = sharedPreferences.getBoolean(key, false)
        } else if (key == resources.getString(R.string.pref_units_key)) {
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
                launchAutomaticUpdates()
            } else {
                cancelAutomaticUpdates()
            }
        }
        if (units != unitsNew) {
            GlobalScope.launch(NonCancellable) {
                repository.updateCurrents(forceUpdate = true)
            }
        }
    }

    private fun launchAutomaticUpdates() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                //.setRequiresDeviceIdle(true)
                .build()

        val repeatingRequest =
                PeriodicWorkRequestBuilder<RefreshDataWorker>(
                        Constants.PERIODIC_REQUEST_DELAY_MINS, TimeUnit.MINUTES
                ).setConstraints(constraints).build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
        )
    }

    private fun cancelAutomaticUpdates() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(RefreshDataWorker.WORK_NAME)
    }
}