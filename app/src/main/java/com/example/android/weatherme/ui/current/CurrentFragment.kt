package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentFragment : Fragment() {

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
            setupTempChart(perHour, tempChart)
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
        viewModel.loadNewCurrent.postValue(arguments.selectedCurrent)
    }

    private fun setupTempChart(perHour: PerHourWithHourly, chart: LineChart) {
        val entries = perHour.hourlyEntities.mapIndexed { index, hourlyEntity ->
            Entry(index.toFloat(), hourlyEntity.temp.toFloat())
        }
        Log.d("CurrentFragment", "chart: $entries")
        val dataSet = LineDataSet(entries, "Temperature")
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.lineWidth = 3f
        dataSet.fillColor = R.color.black
        chart.data = LineData(dataSet)
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setNoDataText("No data to show")
        chart.invalidate()
    }
}