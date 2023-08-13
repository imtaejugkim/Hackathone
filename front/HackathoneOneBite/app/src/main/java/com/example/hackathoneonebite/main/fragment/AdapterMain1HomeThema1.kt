package com.example.hackathoneonebite.main.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeThema1Binding
import com.example.hackathoneonebite.databinding.ItemMain1PostThema1Binding

class AdapterMain1HomeThema1(val context: Context,  val data:ArrayList<Post>)
    : RecyclerView.Adapter<AdapterMain1HomeThema1.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
    interface MusicPlayButtonClickListener {
        fun OnItemClick(position: Int, isMusicPlaying: Boolean, musicNum: Int)
    }
    var itemClickListener: OnItemClickListener? = null
    var musicPlayButtonClickListener: MusicPlayButtonClickListener? = null
    //frame 회전
    val rotateTime: Long = 600 //0.6초
    var isRotating: Boolean = false
    //cd 회전
    val rotationSpeed = 480f //초당 480도
    val cdOuterImageChangeTime = 400 // 0.4초
    var currentlyPlayingViewHolder: ViewHolder? = null

    inner class ViewHolder(val binding: ItemMain1PostThema1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        var isMusicPlaying: Boolean = false
        lateinit var continuousRotationAnimator: ValueAnimator

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
            binding.postImageLayoutBack.playButton.setOnClickListener {
                val cdView: View = binding.postImageLayoutBack.cdImageView
                if(!isMusicPlaying) {
                    isMusicPlaying = !isMusicPlaying
                    //cd 회전
                    continuousRotationAnimator = ValueAnimator.ofFloat(cdView.rotation, cdView.rotation + 360f).apply {
                        duration = (360f / rotationSpeed * 1000).toLong()
                        interpolator = LinearInterpolator()
                        repeatCount = ValueAnimator.INFINITE
                        addUpdateListener { animation ->
                            val animatedValue = animation.animatedValue as Float
                            cdView.rotation = animatedValue
                        }
                    }
                    continuousRotationAnimator.start()
                    currentlyPlayingViewHolder = this@ViewHolder

                    //play 시 cd 테두리 색 변경
                    binding.postImageLayoutBack.cdOuterWhenPlaying.animate()
                        .alpha(1f)
                        .setDuration(cdOuterImageChangeTime.toLong())
                        .setListener(null)

                    //음악 재생
                    musicPlayButtonClickListener?.OnItemClick(adapterPosition, isMusicPlaying, data[adapterPosition].musicNum)
                }
                else {
                    isMusicPlaying = !isMusicPlaying
                    //음악 재생
                    musicPlayButtonClickListener?.OnItemClick(adapterPosition, isMusicPlaying, data[adapterPosition].musicNum)

                    continuousRotationAnimator.cancel()

                    //stop 시 cd 테두리 색 변경
                    binding.postImageLayoutBack.cdOuterWhenPlaying.animate()
                        .alpha(0f)
                        .setDuration(cdOuterImageChangeTime.toLong())
                        .setListener(null)
                }
            }
        }

        fun stopMusicAnimation() {
            if(isMusicPlaying) {
                continuousRotationAnimator.cancel()
                binding.postImageLayoutBack.cdOuter.setImageResource(R.drawable.cd_outer)
                binding.postImageLayoutBack.cdImageView.rotation = 0f
                binding.postImageLayoutBack.cdOuterWhenPlaying.alpha = 0f
                isMusicPlaying = false
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
        //cd회전
        holder.stopMusicAnimation()

        holder.binding.apply {
            //cd회전
            holder.stopMusicAnimation()
            postImageLayoutBack.cdImageView.rotation = 0f
            postImageLayoutBack.cdOuterWhenPlaying.alpha = 0f

            if(data[position].isFliped) {
                postImageLayout.postFrame.visibility = View.INVISIBLE
                postImageLayoutBack.postFrameBack.visibility = View.VISIBLE
            } else {
                postImageLayout.postFrame.rotationY = 0f
                postImageLayout.postFrame.visibility = View.VISIBLE
                postImageLayoutBack.postFrameBack.visibility = View.INVISIBLE
            }

            var imgArray = data[position].imgArray
            Glide.with(context)
                .load(imgArray[0])
                .into(postImageLayout.imageView1frame1)
            Glide.with(context)
                .load(imgArray[1])
                .into(postImageLayout.imageView2frame1)
            Glide.with(context)
                .load(imgArray[2])
                .into(postImageLayout.imageView3frame1)
            Glide.with(context)
                .load(imgArray[3])
                .into(postImageLayout.imageView4frame1)
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