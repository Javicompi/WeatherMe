package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.example.android.weatherme.ui.base.ChartFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentFragment : ChartFragment() {

    private val viewModel: CurrentViewModel by viewModels()

    private val arguments: CurrentFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.loadedCurrent.currentFab.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear, context?.theme)
        )
        binding.loadedCurrent.currentFab.setOnClickListener {
            viewModel.deleteEntry()
        }

        val tempChart = binding.loadedCurrent.tempChartView

        viewModel.perHour.observe(viewLifecycleOwner, { perHour ->
            perHour?.let {
                setupTempChart(perHour, tempChart)
            }
        })

        viewModel.showSnackBar.observe(viewLifecycleOwner, { message ->
            Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, { value ->
            Snackbar.make(this.requireView(), getString(value), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCurrent(arguments.selectedCurrent)
    }
}