package com.example.android.weatherme.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.android.weatherme.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UpcomingFragment : Fragment() {

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var pager: ViewPager2

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = PagerAdapter(this)
        pager = view.findViewById<ViewPager2>(R.id.upcoming_pager)
        pager.adapter = pagerAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.upcoming_tab)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = if (position == 0) {
                "48 hours"
            } else {
                "5 days"
            }
        }.attach()
    }
}