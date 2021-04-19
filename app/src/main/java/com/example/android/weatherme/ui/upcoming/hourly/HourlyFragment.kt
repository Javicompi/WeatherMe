package com.example.android.weatherme.ui.upcoming.hourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentHourlyBinding

class HourlyFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHourlyBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }
}