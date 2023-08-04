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
import android.widget.AdapterView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ItemMain1PostBinding

class AdapterMain1Home(val data:ArrayList<Post>)
    : RecyclerView.Adapter<AdapterMain1Home.ViewHolder>() {
    /*interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }*/

    //var itemClickListener: AdapterView.OnItemClickListener? = null

    inner class ViewHolder(val binding: ItemMain1PostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.postImageLayout.postFrame.setOnClickListener {
                flipView(it)
            }
            binding.postImageLayout2.postFrame.setOnClickListener {
                flipView(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMain1PostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if(data[position].frame == "frame1") {
                Log.d("notify", "frame1입니다.")
                postImageLayout.postFrame.visibility = View.VISIBLE
                postImageLayout2.postFrame.visibility = View.INVISIBLE

                var imgArray = data[position].imgArray
                postImageLayout.imageView1frame1.setImageResource(imgArray[0])
                postImageLayout.imageView2frame1.setImageResource(imgArray[1])
                postImageLayout.imageView3frame1.setImageResource(imgArray[2])
                postImageLayout.imageView4frame1.setImageResource(imgArray[3])
                userIDTextView.text = data[position].id
                messageTextView.text = data[position].message
            } else if(data[position].frame == "frame2") {
                Log.d("notify", "frame2입니다.")
                postImageLayout2.postFrame.visibility = View.VISIBLE
                postImageLayout.postFrame.visibility = View.INVISIBLE

                var imgArray = data[position].imgArray
                postImageLayout2.imageView1frame2.setImageResource(imgArray[0])
                postImageLayout2.imageView2frame2.setImageResource(imgArray[1])
                postImageLayout2.imageView3frame2.setImageResource(imgArray[2])
                postImageLayout2.imageView4frame2.setImageResource(imgArray[3])
                userIDTextView.text = data[position].id
                messageTextView.text = data[position].message
            }
        }
    }

    fun flipView(view: View) {
        val scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", 0f)
        scaleXAnimation.duration = 500
        scaleXAnimation.interpolator = DecelerateInterpolator()
        scaleXAnimation.repeatMode = ValueAnimator.REVERSE
        scaleXAnimation.repeatCount = 1

        val rotationYAnimation = ObjectAnimator.ofFloat(view, "rotationY", 180f)
        rotationYAnimation.duration = 1000
        rotationYAnimation.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(rotationYAnimation)
        animatorSet.start()
    }
}