package com.example.hackathoneonebite.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hackathoneonebite.main.fragment.Main1HomeFragment
import com.example.hackathoneonebite.main.fragment.Main2SearchFragment
import com.example.hackathoneonebite.main.fragment.Main3PostingFragment
import com.example.hackathoneonebite.main.fragment.Main4RankingFragment
import com.example.hackathoneonebite.main.fragment.Main5ProfileFragment

class ViewPageAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 5;
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> Main1HomeFragment()
            1 -> Main2SearchFragment()
            2 -> Main3PostingFragment()
            3 -> Main4RankingFragment()
            4 -> Main5ProfileFragment()
            else -> Main1HomeFragment()
        }
    }
}