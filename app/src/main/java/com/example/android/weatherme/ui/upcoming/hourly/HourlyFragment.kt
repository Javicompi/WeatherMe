package com.example.android.weatherme.ui.upcoming.hourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentHourlyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HourlyFragment : Fragment() {

    private val viewModel: HourlyViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHourlyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = HourlyAdapter()
        binding.hourlyRecycler.adapter = adapter

        viewModel.hourlyList.observe(viewLifecycleOwner, { hourlyList ->
            hourlyList.let {
                adapter.submitList(hourlyList)
            }
        })

        return binding.root
    }
}