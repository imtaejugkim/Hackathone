package com.example.hackathoneonebite.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeFirstBinding
import com.example.hackathoneonebite.main.fragment.AdapterMain1HomeThema1
import com.example.hackathoneonebite.main.fragment.Main1HomeFirstFragment
import com.example.hackathoneonebite.main.fragment.Main1HomeFragment
import com.example.hackathoneonebite.main.fragment.Main2SearchFragment
import com.example.hackathoneonebite.main.fragment.Main3PostingFragment
import com.example.hackathoneonebite.main.fragment.Main4RankingFragment
import com.example.hackathoneonebite.main.fragment.Main5ProfileFragment

class ViewPageAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    interface OnFragmentButtonClickListener {
        fun onButtonClick(position: Int, newFragment: Fragment, themaNum: Int)
    }
    private var buttonClickListener: OnFragmentButtonClickListener? = null
    fun setOnFragmentButtonClickListener(listener: OnFragmentButtonClickListener) {
        this.buttonClickListener = listener
    }

    private val fragments: MutableList<Fragment> = mutableListOf(
        Main1HomeFirstFragment(),
        Main2SearchFragment(),
        Main3PostingFragment(),
        Main4RankingFragment(),
        Main5ProfileFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    private var currentListIds = mutableListOf<Long>()
    init {
        // Assign unique IDs for each initial fragments
        currentListIds = MutableList(fragments.size) { it.toLong() }
    }

    override fun getItemId(position: Int): Long {
        return currentListIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return currentListIds.contains(itemId)
    }

    fun replaceFragment(position: Int, newFragment: Fragment, themaNum: Int) {
        if (position < fragments.size) {
            fragments[position] = newFragment
            currentListIds[position] = System.currentTimeMillis()
            addDataToFragment(position, "themaNum", themaNum)
            notifyItemChanged(position)
        }
    }
    fun addDataToFragment(position: Int, key: String, value: Int) {
        val fragment = fragments[position]
        val args = fragment.arguments ?: Bundle()
        args.putInt(key, value)
        fragment.arguments = args
    }
}