package com.example.android.weatherme.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.android.weatherme.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}