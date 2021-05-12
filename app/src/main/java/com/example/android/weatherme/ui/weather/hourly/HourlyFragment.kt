package com.example.android.weatherme.ui.weather.hourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentHourlyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HourlyFragment : Fragment() {

    private val viewModel: HourlyViewModel by viewModels()

    private val arguments: HourlyFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHourlyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val columnCount = resources.getInteger(R.integer.grid_column_count)
        binding.hourlyRecycler.layoutManager = GridLayoutManager(activity, columnCount)

        val adapter = HourlyAdapter()
        binding.hourlyRecycler.adapter = adapter

        viewModel.hourlyList.observe(viewLifecycleOwner, { hourlyList ->
            hourlyList.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadHourlys(arguments.selectedCurrent)
    }
}