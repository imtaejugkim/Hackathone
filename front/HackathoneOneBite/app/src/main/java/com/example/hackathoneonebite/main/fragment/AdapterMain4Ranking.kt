package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Rank
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.LoadRanking
import com.example.hackathoneonebite.databinding.ItemRankingListBinding

class AdapterMain4Ranking(val context: Context, val dataList: MutableList<LoadRanking> = arrayListOf()) : RecyclerView.Adapter<AdapterMain4Ranking.ViewHolder>() {
    val baseUrl = "http://203.252.139.231:8080/"
    interface OnItemClickListener{
        fun OnItemClick(position:Int)
    }
    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: ItemRankingListBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.touchRegion.setOnClickListener {
                itemClickListener?.OnItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRankingListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount():Int{
        return dataList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Log.e("MAIN4RANKING_CREATE_COMMENT2.5", dataList.toString())
            rankText.text = (position + 1).toString()
            Glide.with(context)
                .load(baseUrl + dataList[position].profileUrl)
                .into(rankingProfile)
            rankName.text = dataList[position].username
            scoreText.text = dataList[position].score.toString()
        }
    }
}