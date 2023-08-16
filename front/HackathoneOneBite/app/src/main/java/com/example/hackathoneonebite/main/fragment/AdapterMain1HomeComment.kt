package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.databinding.ItemMain1CommentBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterMain1HomeComment (val context: Context, var data:MutableList<CommentResponse> = mutableListOf<CommentResponse>())
    : RecyclerView.Adapter<AdapterMain1HomeComment.ViewHolder>() {
    val baseUrl: String = "http://203.252.139.231:8080/"
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
            dateTextView.text = calculateTimeDifference(data[position].time)
        }
    }
    //타임 차이 계산
    private fun calculateTimeDifference(time: String): String {
        var beforeTime = time
        if (beforeTime.length < 26) {
            for (i in 0..25 - time.length) {
                beforeTime += "0"
            }
            val parts = beforeTime.split(".")
            val nanoSeconds = ((parts[1]).toInt() - 1).toString()
            beforeTime = parts[0] + "." + nanoSeconds
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val timeObject = LocalDateTime.parse(beforeTime, formatter)

        val duration = Duration.between(timeObject, LocalDateTime.now())

        val days = duration.toDays()
        val hours = duration.toHours() - days * 24
        val minutes = duration.toMinutes() - days * 24 * 60 - hours * 60

        return when {
            days > 0 -> "$days 일 전"
            hours > 0 -> "$hours 시간 전"
            minutes > 0 -> "$minutes 분 전"
            else -> "방금 전"
        }
    }
}