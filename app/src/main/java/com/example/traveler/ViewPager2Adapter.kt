package com.example.traveler

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    private val fragmentList: MutableList<Fragment> = mutableListOf(FirstFragment(),SecondFragment(),ThirdFragment(),MapFragment())
    private val itemCount = fragmentList.size
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            2 -> ThirdFragment()
            3 -> MapFragment()
            else -> throw IndexOutOfBoundsException("Invalid fragment position")
        }
    }
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }
}