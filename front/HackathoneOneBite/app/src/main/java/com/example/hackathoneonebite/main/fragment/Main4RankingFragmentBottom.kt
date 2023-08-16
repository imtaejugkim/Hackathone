package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.Data.Rank
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding

class Main4RankingFragmentBottom : Fragment() {

    lateinit var binding : FragmentMain4RankingBottomBinding
    private lateinit var viewModel: Main4RankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingBottomBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[Main4RankingViewModel::class.java]

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //val adapter = AdapterMain4Ranking(viewModel.dataList?.subList(5, 49) ?: emptyList())
        //recyclerView.adapter = adapter
        Log.d("바텀 잘 왔니", viewModel.dataList.toString())

        return binding.root
    }
}