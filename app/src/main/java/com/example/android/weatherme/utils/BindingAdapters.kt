package com.example.android.weatherme.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.weatherme.R
import java.util.*
import kotlin.math.roundToInt

object BindingAdapters {

    @BindingAdapter("android:currentBackground")
    @JvmStatic
    fun View.setCurrentBackground(icon: String) {
        icon.let {
            val context = this.context
            if (icon == "_01d" || icon == "_02d") {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_dayclear))
            } else if (icon == "_03d" || icon == "_10d" || icon == "_50d") {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_dayscatered))
            } else if (icon == "_04d" || icon == "_09d" || icon == "_11d" || icon == "_13d") {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_daycloudy))
            } else if (icon == "_01n" || icon == "_02n") {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_nightclear))
            } else {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_nightcloudy))
            }
        }
    }

    @BindingAdapter("android:currentIcon")
    @JvmStatic
    fun ImageView.setCurrentIcon(icon: String?) {
        icon.let {
            if (!icon.isNullOrEmpty()) {
                val context = this.context
                val resourceName = icon.replace("_", "i")
                val resourceId = context.resIdByName(resourceName, "drawable")
                this.setImageResource(resourceId)
            }
        }
    }

    @BindingAdapter("android:currentBackground")
    @JvmStatic
    fun ImageView.setCurrentBackground(icon: String?) {
        icon.let {
            if (!icon.isNullOrEmpty()) {
                val context = this.context
                val resourceName = icon.replace("_", "f")
                val resourceId = context.resIdByName(resourceName, "drawable")
                this.setImageResource(resourceId)
            }
        }
    }

    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    @BindingAdapter("android:windDirectionString")
    @JvmStatic
    fun TextView.degreesToString(value: Int) {
        val degreesArray = resources.getStringArray(R.array.wind_directions_strings)
        var index = (value / 22.5).roundToInt()
        if (index == 16) {index = 0 }
        text = degreesArray[index]
    }

    @BindingAdapter("android:windDirection")
    @JvmStatic
    fun ImageView.direction(value: Int) {
        rotation = (value + 180).toFloat()
    }

    @BindingAdapter(value = ["android:time", "android:offset"], requireAll = true)
    @JvmStatic
    fun TextView.valueToDate(time: Long, offset: Int) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeZone.rawOffset = offset
        calendar.timeInMillis = time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
        text = dateFormat.format(calendar)
    }
}