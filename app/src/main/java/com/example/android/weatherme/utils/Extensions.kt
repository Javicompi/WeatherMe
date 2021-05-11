package com.example.android.weatherme.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.data.database.entities.daily.DailyEntity
import com.example.android.weatherme.data.database.entities.hourly.HourlyEntity
import java.util.*
import java.util.concurrent.TimeUnit

//Animate changing the view visibility
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

//Animate changing the view visibility
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}

//Check there is connectivity
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw      = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

//Usage: val drawableResId = context.resIdByName("ic_edit_black_24dp", "drawable")
fun Context.resIdByName(resIdName: String?, resType: String): Int {
    resIdName?.let {
        return resources.getIdentifier(it, resType, packageName)
    }
    throw Resources.NotFoundException()
}

fun shouldUpdate(time: Long): Boolean {
    val difference = TimeUnit.MINUTES.toMillis(Constants.PERIODIC_REQUEST_DELAY_MINS)
    return System.currentTimeMillis() - difference > time
}

fun longToStringTime(time: Long, offset: Int): String {
    val date = Date(time)
    date.time += offset.toLong()
    val dateFormat = SimpleDateFormat("HH:mm")
    dateFormat.timeZone = TimeZone.GMT_ZONE
    return dateFormat.format(date)
}

fun createDefaultHourlys(current: CurrentEntity): List<HourlyEntity> {
    val hourlyList = arrayListOf<HourlyEntity>()
    val hourlyEntity = HourlyEntity(
            id = null,
            cityId = current.cityId,
            lat = current.latitude,
            lon = current.longitude,
            deltaTime = 0,
            timezone = "",
            offset = current.timeZone,
            temp = 0,
            feelsLike = 0,
            pressure = 0,
            humidity = 0,
            dewPoint = 0.0,
            uvi = 0.0,
            clouds = 0,
            visibility = 0,
            windSpeed = 0,
            windDegrees = 0,
            pop = 0,
            weatherId = 0,
            shortDescription = "",
            description = "",
            icon = ""
    )
    hourlyList.add(hourlyEntity)
    return hourlyList
}

fun createDefaultDailys(current: CurrentEntity): List<DailyEntity> {
    val dailyList = arrayListOf<DailyEntity>()
    val dailyEntity = DailyEntity(
        id = null,
        cityId = current.cityId,
        lat = current.latitude,
        lon = current.longitude,
        deltaTime = 0,
        timezone = "",
        offset = current.timeZone,
        sunrise = 0,
        sunset = 0,
        moonrise = 0,
        moonset = 0,
        moonPhase = 0.0,
        tempMax = 0,
        tempMin = 0,
        tempMorn = 0,
        tempDay = 0,
        tempNight = 0,
        tempEve = 0,
        feelsLikeMorn = 0,
        feelsLikeDay = 0,
        feelsLikeNight = 0,
        feelsLikeEve = 0,
        pressure = 0,
        humidity = 0,
        dewPoint = 0.0,
        windSpeed = 0,
        windDegrees = 0,
        windGust = 0,
        clouds = 0,
        pop = 0,
        rain = 0.0,
        snow = 0.0,
        uvi = 0.0,
        weatherId = 0,
        description = "",
        shortDescription = "",
        icon = ""
    )
    dailyList.add(dailyEntity)
    return dailyList
}