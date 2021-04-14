package com.example.android.weatherme.ui.upcoming

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.weatherme.ui.upcoming.daily.DailyFragment
import com.example.android.weatherme.ui.upcoming.hourly.HourlyFragment

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
            HourlyFragment(),
            DailyFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}