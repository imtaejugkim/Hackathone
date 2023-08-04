package com.example.hackathoneonebite.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFrameActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainFrameBinding
    val imgArray = arrayListOf<Int>(
        R.drawable.tab_main,R.drawable.tab_search,R.drawable.tab_post,R.drawable.tab_rank,R.drawable.tab_profile)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }
    private fun initLayout() {

        binding.mainViewPager.adapter = ViewPageAdapter(this)
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) {
                tab, pos ->
            tab.setIcon(imgArray[pos])
        }.attach()
        binding.mainViewPager.registerOnPageChangeCallback (object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.mainViewPager.isUserInputEnabled = false
                } else {
                    binding.mainViewPager.isUserInputEnabled = true
                }
            }
        })
    }
}
