package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingTimeBinding

class Main3PostingTimeActivity : AppCompatActivity() {

    lateinit var binding : ActivityMain3PostingTimeBinding
    private val numImages = 10
    private val currentIndexArray = IntArray(numImages)
    private val imageResources = arrayOf(
        R.drawable.img_0,
        R.drawable.img_1,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_4,
        R.drawable.img_4,
        R.drawable.img_4,
        R.drawable.img_4,
        R.drawable.img_4
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        binding.selectName.text = "$selectedName"

        if (receivedPost != null) {
            // Post 객체의 정보 받아서 처리
        }

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val rightArrow = binding.rightArrow
        rightArrow.setOnClickListener {
            val nextIntent = Intent(this, Main3PostingRequestActivity::class.java)
            nextIntent.putExtra("selected_name", selectedName)
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        val imageViews = arrayOf(
            binding.imageView1,
            binding.imageView2,
            binding.imageView3,
            binding.imageView4
        )
        for ((index, imageView) in imageViews.withIndex()) {
            imageView.setOnClickListener {
                val anim = AnimationUtils.loadAnimation(this, R.anim.slide_time)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        currentIndexArray[index] = (currentIndexArray[index] + 1) % numImages
                        imageView.setImageResource(imageResources[currentIndexArray[index]])

                    }
                    override fun onAnimationEnd(animation: Animation?) {
                        imageView.setImageResource(imageResources[currentIndexArray[index]])
                        imageView.clearAnimation()
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                imageView.startAnimation(anim)
            }
        }
    }
}