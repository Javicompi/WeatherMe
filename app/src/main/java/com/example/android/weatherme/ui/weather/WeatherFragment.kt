package com.example.android.weatherme.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.android.weatherme.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WeatherFragment : Fragment() {

    private lateinit var pagerAdapter: WeatherPagerAdapter
    private lateinit var pager: ViewPager2

    private val arguments: WeatherFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments.selectedCurrent
        pagerAdapter = WeatherPagerAdapter(this, id)
        pager = view.findViewById(R.id.upcoming_pager)
        pager.adapter = pagerAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.upcoming_tab)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = if (position == 0) {
                getString(R.string.title_current)
            } else if (position == 1) {
                getString(R.string.title_48_hours)
            } else {
                getString(R.string.title_7_days)
            }
        }.attach()
    }
}