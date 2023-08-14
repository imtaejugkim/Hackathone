package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding

class Main4RankingFragmentBottom : Fragment() {

    lateinit var binding : FragmentMain4RankingBottomBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingBottomBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}