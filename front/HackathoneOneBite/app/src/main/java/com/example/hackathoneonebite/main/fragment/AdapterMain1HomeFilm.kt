package com.example.hackathoneonebite.main.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.databinding.ItemMain1PostFilmBinding

class AdapterMain1HomeFilm(val data:ArrayList<Post>)
    : RecyclerView.Adapter<AdapterMain1HomeFilm.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemMain1PostFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.postImageLayout.postFrame.setOnClickListener {
                itemClickListener?.OnItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMain1PostFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            postImageLayout.postFrame.rotationY = if(data[position].isFliped) 180f else 0f

            var imgArray = data[position].imgArray
            postImageLayout.imageView1.setImageResource(imgArray[0].toInt())
            postImageLayout.imageView2.setImageResource(imgArray[1].toInt())
            postImageLayout.imageView3.setImageResource(imgArray[2].toInt())
            postImageLayout.imageView4.setImageResource(imgArray[3].toInt())
        }
    }
}