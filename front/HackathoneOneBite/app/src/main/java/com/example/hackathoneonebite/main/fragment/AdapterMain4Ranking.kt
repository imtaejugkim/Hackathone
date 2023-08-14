package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Rank
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ItemRankingListBinding
import com.google.android.material.imageview.ShapeableImageView

// ... (다른 import 문들)
class AdapterMain4Ranking(private val dataList: List<Rank>) :
    RecyclerView.Adapter<AdapterMain4Ranking.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]

        if (position == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight))
            holder.bind(data, position + 1) // 순위를 1로 설정하여 표시
        } else {
            holder.bind(data, position + 2) // 나머지 순위는 2부터 표시
        }
    }

    override fun getItemCount(): Int {
        return minOf(dataList.size, 4)
    }

    inner class ViewHolder(val binding: ItemRankingListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val rankText = binding.rankText
        private val rankingProfile = binding.rankingProfile
        private val rankName = binding.rankName
        private val followText = binding.followText

        fun bind(data: Rank, rank: Int) {
            rankText.text = data.rankText.toString()
            rankName.text = data.rankName
            rankingProfile.setImageResource(R.drawable.test_image1)
            followText.text = data.followText.toString()
        }
    }
}
