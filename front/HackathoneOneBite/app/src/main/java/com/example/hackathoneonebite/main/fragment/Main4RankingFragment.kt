package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBinding
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding

class Main4RankingFragment : Fragment() {

    lateinit var binding : FragmentMain4RankingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingBinding.inflate(layoutInflater, container, false)

        val viewPager: ViewPager2 = binding.viewPager
        val adapter = AdapterMain4RankingFragment(requireActivity())
        viewPager.adapter = adapter

        return binding.root
    }
}