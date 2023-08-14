package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.Data.Rank
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain4RankingBottomBinding
import com.example.hackathoneonebite.databinding.FragmentMain4RankingTopBinding
import java.util.Random

class Main4RankingFragmentTop : Fragment() {

    private lateinit var viewModel: Main4RankingViewModel
    lateinit var binding : FragmentMain4RankingTopBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingTopBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(Main4RankingViewModel::class.java)
        val random = Random()

        val yourFollowCount = random.nextInt(1000000) + 1
        val yourData = Rank(0, yourFollowCount, "홍길동")

        val dataList = mutableListOf<Rank>()

        for (i in 1..49) {
            val followCount = random.nextInt(1000000) + 1
            val rankName = "User$i"

            dataList.add(Rank(0, followCount, rankName))
        }

        dataList.add(yourData)

        val sortedDataList = dataList.sortedByDescending { it.followText }.toMutableList()
        val myRank = sortedDataList.indexOfFirst { it == yourData } + 1

        for ((index, data) in sortedDataList.withIndex()) {
            sortedDataList[index] = data.copy(rankText = index + 1)
        }

        binding.myRankName.text = yourData.rankName
        binding.myFollowText.text = yourData.followText.toString()
        binding.myRankText.text = myRank.toString()

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdapterMain4Ranking(sortedDataList)
        recyclerView.adapter = adapter

        return binding.root
    }
}