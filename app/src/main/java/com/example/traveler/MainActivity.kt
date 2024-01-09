package com.example.traveler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


public class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        viewPager.isUserInputEnabled = false
        viewPager.setPageTransformer { page, position ->
            if (position < -1 || position > 1) {
                page.alpha = 0f
            } else {
                val scale = Math.max(0.85f, 1 - Math.abs(position))
                page.scaleX = scale
                page.scaleY = scale
                page.alpha = Math.max(0.5f, 1 - Math.abs(position))
            }
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager.adapter = ViewPager2Adapter(this)
        viewPager.setCurrentItem(0,false)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = "Tab1"
//                1 -> tab.text = "Tab2"
//                2 -> tab.text = "Tab3"
//            }
            // 커스텀 뷰를 인플레이트
            val tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
            val tabIcon = tabView.findViewById<ImageView>(R.id.tab_icon)

            when (position) {
                0 -> {
                    tabIcon.setImageResource(R.drawable.round_home_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.select))
                }
                1 -> {
                    tabIcon.setImageResource(R.drawable.round_favorite_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black))
                }
                2 -> {
                    tabIcon.setImageResource(R.drawable.round_person_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black))
                }
                3 -> {
                    tabIcon.setImageResource(R.drawable.round_map_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black))
                }
                4 -> {
                    tabIcon.setImageResource(R.drawable.round_favorite_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black))
                }
                5 -> {
                    tabIcon.setImageResource(R.drawable.round_favorite_24)
                    tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.black))
                }
            }
            // 탭에 커스텀 뷰를 설정
            tab.customView = tabView
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabIcon = tab.customView?.findViewById<ImageView>(R.id.tab_icon)
                tabIcon?.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.select))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabIcon = tab.customView?.findViewById<ImageView>(R.id.tab_icon)
                tabIcon?.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.unselect))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 재선택될 때 필요한 동작이 있으면 여기에 구현
            }
        })
    }
}