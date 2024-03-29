package com.example.traveler

import TripFragment
import android.renderscript.ScriptGroup.Input
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var itemCount = 3

    override fun getItemCount(): Int {
        return itemCount
    }
    fun updateItemCount(newCount: Int) {
        itemCount = newCount
        notifyDataSetChanged() // 아이템 개수 변경을 알림
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SecondFragment()
            1 -> FirstFragment()
            2 -> ThirdFragment()
//            3 -> MapFragment()
//            4 -> TripFragment()
            3 -> InputFragment()
//            6 -> SearchPlaceFragment()
            else -> throw IndexOutOfBoundsException("Invalid fragment position")
        }
    }
}