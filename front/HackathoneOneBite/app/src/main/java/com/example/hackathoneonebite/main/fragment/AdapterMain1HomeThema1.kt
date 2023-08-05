package com.example.hackathoneonebite.main.fragment

import android.animation.Animator
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
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.databinding.ItemMain1PostThema1Binding

class AdapterMain1HomeThema1(val data:ArrayList<Post>)
    : RecyclerView.Adapter<AdapterMain1HomeThema1.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
    var itemClickListener: OnItemClickListener? = null
    val rotateTime: Long = 600
    var isRotating: Boolean = false

    inner class ViewHolder(val binding: ItemMain1PostThema1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.postImageLayout.touchView.setOnClickListener {
                if(!isRotating) {
                    flipView(binding.postImageLayout.postFrame, binding.postImageLayoutBack.postFrameBack)
                    itemClickListener?.OnItemClick(adapterPosition)
                }
            }
            binding.postImageLayoutBack.touchView.setOnClickListener {
                if(!isRotating) {
                    flipView(binding.postImageLayoutBack.postFrameBack, binding.postImageLayout.postFrame)
                    itemClickListener?.OnItemClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMain1PostThema1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if(data[position].isFliped) {
                postImageLayout.postFrame.visibility = View.INVISIBLE
                postImageLayoutBack.postFrameBack.visibility = View.VISIBLE
            } else {
                postImageLayout.postFrame.rotationY = 0f
                postImageLayout.postFrame.visibility = View.VISIBLE
                postImageLayoutBack.postFrameBack.visibility = View.INVISIBLE
            }

            var imgArray = data[position].imgArray
            postImageLayout.imageView1.setImageResource(imgArray[0])
            postImageLayout.imageView2.setImageResource(imgArray[1])
            postImageLayout.imageView3.setImageResource(imgArray[2])
            postImageLayout.imageView4.setImageResource(imgArray[3])
        }
    }

    fun flipView(frontView: View, backView: View) {
        val first = ObjectAnimator.ofFloat(frontView, "rotationY", 90f)
        first.duration = rotateTime / 2
        first.interpolator = AccelerateDecelerateInterpolator()

        val second = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f)
        second.duration = rotateTime / 2
        second.interpolator = AccelerateDecelerateInterpolator()

        first.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isRotating = true
            }
            override fun onAnimationEnd(animation: Animator) {
                frontView.visibility = View.INVISIBLE
                backView.visibility = View.VISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        second.addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }
            override fun onAnimationEnd(animation: Animator) {
                isRotating = false
            }
            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(first, second)
        animatorSet.start()
    }
}