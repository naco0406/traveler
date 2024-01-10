package com.example.traveler

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.navercorp.nid.NaverIdLoginSDK
import org.json.JSONObject
import java.io.File

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            "Traveler"
        )
    }
}

public class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileName = "user_status.json"
        val internalStorageDir = this.applicationContext.filesDir
        val file = File(internalStorageDir, fileName)

        if (file.exists()) {
            // 파일이 존재하면 기존 데이터를 읽어옴
            try {
                val content = file.readText()
                val userData = JSONObject(content)
                val loginState = userData.optBoolean("login_state", false)
                // loginState 변수를 사용하여 로그인 상태를 초기화하거나 필요한 작업 수행
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // 파일이 없을 경우 새로운 user_status.json 파일을 생성하고 초기 상태를 설정
            val initialData = JSONObject().apply {
                put("login_state", false)
                // 기타 초기 데이터 필드 추가 가능
            }
            file.writeText(initialData.toString())
        }

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