package com.example.android.weatherme.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.weatherme.ui.current.CurrentFragment
import com.example.android.weatherme.ui.weather.daily.DailyFragment
import com.example.android.weatherme.ui.weather.hourly.HourlyFragment

class WeatherPagerAdapter(fragment: Fragment, private val id: Long) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
            CurrentFragment(),
            HourlyFragment(),
            DailyFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragments[position]
        fragment.arguments = Bundle().apply {
            putLong("selectedCurrent", id)
        }
        return fragment
    }
}