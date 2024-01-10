package com.example.traveler

import TripFragment
import android.renderscript.ScriptGroup.Input
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    private val fragmentList: MutableList<Fragment> = mutableListOf(SecondFragment(),FirstFragment(),ThirdFragment(),InputFragment())
    private val itemCount = fragmentList.size
    override fun getItemCount(): Int {
        return itemCount
    }

    fun getFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun createFragment(position: Int): Fragment {
        // 해당 위치의 Fragment 반환
        return fragmentList[position]
    }
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }
}