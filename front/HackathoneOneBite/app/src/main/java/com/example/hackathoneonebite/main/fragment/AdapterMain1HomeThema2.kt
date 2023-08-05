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
import com.example.hackathoneonebite.databinding.ItemMain1PostThema2Binding

class AdapterMain1HomeThema2 (val data:ArrayList<Post>)
    : RecyclerView.Adapter<AdapterMain1HomeThema2.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemMain1PostThema2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.postImageLayout.postFrame.setOnClickListener {
                flipView(it)
                itemClickListener?.OnItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMain1PostThema2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            postImageLayout.postFrame.rotationY = if(data[position].isFliped) 180f else 0f
            Log.i("정보",data[position].isFliped.toString())

            var imgArray = data[position].imgArray
            postImageLayout.imageView1.setImageResource(imgArray[0])
            postImageLayout.imageView2.setImageResource(imgArray[1])
            postImageLayout.imageView3.setImageResource(imgArray[2])
            postImageLayout.imageView4.setImageResource(imgArray[3])
        }
    }

    fun flipView(view: View) {
        val rotationYAnimation = ObjectAnimator.ofFloat(view, "rotationY", 180f)
        rotationYAnimation.duration = 700
        rotationYAnimation.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(rotationYAnimation)
        animatorSet.start()
    }
}