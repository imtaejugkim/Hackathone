package com.example.hackathoneonebite.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFrameActivity : AppCompatActivity(), ViewPageAdapter.OnFragmentButtonClickListener {
    lateinit var binding: ActivityMainFrameBinding
    lateinit var adapter: ViewPageAdapter
    val imgArray = arrayListOf<Int>(
        R.drawable.tab_main,R.drawable.tab_search,R.drawable.tab_post,R.drawable.tab_rank,R.drawable.tab_profile)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }
    private fun initLayout() {
        adapter = ViewPageAdapter(this)
        /*adapter.setOnFragmentButtonClickListener(object : ViewPageAdapter.OnFragmentButtonClickListener {
            override fun onButtonClick(position: Int, newFragment: Fragment) {
                Toast.makeText(this@MainFrameActivity, "클릭!", Toast.LENGTH_SHORT).show()
                adapter.replaceFragment(position, newFragment)
            }
        })*/
        binding.mainViewPager.adapter = adapter

        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) {
                tab, pos ->
            tab.setIcon(imgArray[pos])
        }.attach()
        binding.mainViewPager.registerOnPageChangeCallback (object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.mainViewPager.isUserInputEnabled = false
            }
        })
    }

    override fun onButtonClick(position: Int, newFragment: Fragment, themaNum: Int) {
        Toast.makeText(this@MainFrameActivity, "클릭! GOTO:"+newFragment.toString(), Toast.LENGTH_SHORT).show()
        binding.mainViewPager.currentItem = 0
        adapter.replaceFragment(position, newFragment, themaNum)
    }
}
