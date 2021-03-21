package com.example.android.weatherme.ui.base

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.android.weatherme.data.database.entities.perhour.PerHourWithHourly
import com.example.android.weatherme.utils.TempValueFormatter
import com.example.android.weatherme.utils.TimeValueFormatter
import com.example.android.weatherme.utils.longToStringDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

abstract class ChartFragment : Fragment() {

    fun setupTempChart(perHourWithHourly: PerHourWithHourly, chart: LineChart) {
        val entries = if (perHourWithHourly.hourlyEntities.isNullOrEmpty()) {
            null
        } else {
            perHourWithHourly.hourlyEntities.subList(0, 24).mapIndexed { index, hourlyEntity ->
                Entry(index.toFloat(), hourlyEntity.temp.toFloat())
            }
        }
        val labels = ArrayList<String>()
        for (i in 0..23) {
            val deltaTime = perHourWithHourly.hourlyEntities[i].deltaTime
            val strTime = longToStringDate(deltaTime, perHourWithHourly.perHourEntity.timezone)
            labels.add(strTime)
        }
        Log.d("MainActivity", "entries: ${entries?.size} labels: ${labels.size}")
        val dataSet = LineDataSet(entries, "")
        val temps = entries?.map { entry ->
            entry.x.toString()
        }
        dataSet.valueFormatter = TempValueFormatter(temps!!.toMutableList())
        dataSet.setDrawValues(true)
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 0f
        chart.data = LineData(dataSet)
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setNoDataText("No data to show")
        chart.setTouchEnabled(false)
        chart.setDrawMarkers(false)
        chart.setDrawGridBackground(false)
        chart.setDrawBorders(false)
        chart.description = null
        chart.legend.isEnabled = false
        chart.data.setValueTextSize(12f)
        val xAxis = chart.xAxis
        xAxis.valueFormatter = TimeValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = labels.size
        xAxis.setDrawLabels(true)
        xAxis.textSize = 12f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        chart.invalidate()
    }
}