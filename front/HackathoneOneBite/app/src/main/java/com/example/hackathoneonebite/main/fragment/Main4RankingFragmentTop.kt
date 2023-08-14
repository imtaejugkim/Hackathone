package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding
import com.example.hackathoneonebite.databinding.FragmentMain4RankingTopBinding

class Main4RankingFragmentTop : Fragment() {

    lateinit var binding : FragmentMain4RankingTopBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingTopBinding.inflate(layoutInflater, container, false)
        return binding.root

    }
}