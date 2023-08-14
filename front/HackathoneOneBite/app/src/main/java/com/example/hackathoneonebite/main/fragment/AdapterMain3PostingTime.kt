package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import com.example.hackathoneonebite.R

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterMain3PostingTime(private val timeList: List<String>) :
    RecyclerView.Adapter<AdapterMain3PostingTime.TimeViewHolder>() {

    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time, parent, false)
        return TimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val time = timeList[position]
        holder.timeTextView.text = time
    }

    override fun getItemCount(): Int {
        return timeList.size
    }
}