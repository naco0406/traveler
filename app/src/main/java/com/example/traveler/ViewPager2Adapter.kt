package com.example.traveler

import TripFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    private val fragmentList: MutableList<Fragment> = mutableListOf(FirstFragment(),SecondFragment(),ThirdFragment(),MapFragment())
    private val itemCount = fragmentList.size
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SecondFragment()
            1 -> FirstFragment()
            2 -> ThirdFragment()
            3 -> MapFragment()
            4 -> TripFragment()
            else -> throw IndexOutOfBoundsException("Invalid fragment position")
        }
    }
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }
}