package com.example.android.weatherme.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.contains
import androidx.databinding.BindingAdapter
import com.example.android.weatherme.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

object BindingAdapters {

    @BindingAdapter("android:currentBackground")
    @JvmStatic
    fun View.setCurrentBackground(icon: String) {
        icon.let {
            val context = this.context
            if (icon.equals("_01d") || icon.equals("_02d")) {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_dayclear))
            } else if (icon.equals("_03d") || icon.equals("_10d") || icon.equals("_50d")) {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_dayscatered))
            } else if (icon.equals("_04d") || icon.equals("_09d") || icon.equals("_11d") || icon.equals("_13d")) {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_daycloudy))
            } else if (icon.equals("_01n") || icon.equals("_02n")) {
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

    @BindingAdapter("android:setFabIcon")
    fun FloatingActionButton.setFabIcon(isSaved: Boolean) {
        val context = this.context
        if (isSaved) {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clear))
        } else {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_input_add))
        }
    }
}