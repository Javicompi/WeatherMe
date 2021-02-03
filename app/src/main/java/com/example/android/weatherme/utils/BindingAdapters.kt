package com.example.android.weatherme.utils

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.example.android.weatherme.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

object BindingAdapters {

    @BindingAdapter("android:currentBackground")
    @JvmStatic
    fun CardView.setCurrentBackground(icon: String) {
        icon.let {
            val context = this.context
            if (icon.contains("n")) {
                this.setBackgroundColor(context.getColor(R.color.black))
            } else if (
                    icon.contains("01") ||
                    icon.contains("02") ||
                    icon.contains("03")
            ) {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_dayclear))
            } else {
                this.setBackgroundColor(context.getColor(R.color.list_item_current_background_daydark))
            }
        }
    }

    @BindingAdapter("android:currentIcon")
    @JvmStatic
    fun ImageView.setCurrentIcon(icon: String) {
        icon.let {
            val context = this.context
            val resourceId = context.resIdByName(icon, "drawable")
            this.setImageResource(resourceId)
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