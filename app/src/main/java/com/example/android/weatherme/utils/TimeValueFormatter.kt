package com.example.android.weatherme.utils

import android.icu.text.SimpleDateFormat
import android.util.Log
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.ParseException

class TimeValueFormatter(values: MutableCollection<String>) : IndexAxisValueFormatter(values) {

    override fun getFormattedValue(value: Float): String {
        return try {
            Log.d("StringValueFormatter", "index: ${value.toInt()} value: ${values[value.toInt()]}")
            values[value.toInt()]
        } catch (ex: Exception) {
            Log.e("DateValueFormatter", ex.localizedMessage)
            ""
        }
    }
}