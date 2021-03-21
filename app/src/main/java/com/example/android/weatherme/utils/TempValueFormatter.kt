package com.example.android.weatherme.utils

import android.util.Log
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TempValueFormatter(values: MutableCollection<String>) : IndexAxisValueFormatter(values) {

    override fun getFormattedValue(value: Float): String {
        return try {
            values[value.toInt()].toFloat().toInt().toString() + "°"
        } catch (ex: Exception) {
            Log.e("DateValueFormatter", ex.localizedMessage)
            ""
        }
    }
}