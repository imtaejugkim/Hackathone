package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.databinding.ItemMain1CommentBinding
import com.google.android.play.core.integrity.p

class AdapterMain1HomeComment (val context: Context, var data:MutableList<CommentResponse> = mutableListOf<CommentResponse>())
    : RecyclerView.Adapter<AdapterMain1HomeComment.ViewHolder>() {
    val baseUrl: String = "http://221.146.39.177:8081/"
    interface OnItemClickListener {
        fun OnItemClick(id: Long)
    }
    var userIdClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemMain1CommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.userIdStringTextView.setOnClickListener {
                userIdClickListener?.OnItemClick(data[adapterPosition].commentWriterId)
            }
            binding.profileImageView.setOnClickListener {
                userIdClickListener?.OnItemClick(data[adapterPosition].commentWriterId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMain1CommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context)
                .load(baseUrl + data[position].profileImageUrl)
                .into(profileImageView)
            userIdStringTextView.text = data[position].commentWriterUserId
            commentTextView.text = data[position].text
            dateTextView.text = data[position].time
        }
    }
}