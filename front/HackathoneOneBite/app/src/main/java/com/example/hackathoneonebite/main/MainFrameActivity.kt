package com.example.hackathoneonebite.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMainFrameBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFrameActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainFrameBinding
    val imgArray = arrayListOf<Int>(
        R.drawable.baseline_home_24,R.drawable.baseline_search_24,R.drawable.baseline_exposure_plus_1_24,R.drawable.baseline_star_24,R.drawable.baseline_account_circle_24)
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
    }
}