package com.example.android.weatherme.ui.weather.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentDailyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyFragment : Fragment() {

    private val viewModel: DailyViewModel by viewModels()

    private val arguments: DailyFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDailyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val columnCount = resources.getInteger(R.integer.grid_column_count)
        binding.dailyRecyclerview.layoutManager = GridLayoutManager(activity, columnCount)

        val adapter = DailyAdapter()
        binding.dailyRecyclerview.adapter = adapter

        viewModel.dailyList.observe(viewLifecycleOwner, { dailyList ->
            dailyList.let {
                adapter.submitList(dailyList)
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadDailys(arguments.selectedCurrent)
    }
}