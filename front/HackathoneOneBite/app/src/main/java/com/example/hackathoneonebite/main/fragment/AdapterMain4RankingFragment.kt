package com.example.hackathoneonebite.main.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterMain4RankingFragment(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2 // Top and bottom fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Main4RankingFragmentTop() // Replace with your top fragment
            1 -> Main4RankingFragmentBottom() // Replace with your bottom fragment
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}