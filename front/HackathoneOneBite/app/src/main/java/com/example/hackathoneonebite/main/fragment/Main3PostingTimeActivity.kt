package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingTimeBinding

class Main3PostingTimeActivity : AppCompatActivity() {

    lateinit var binding: ActivityMain3PostingTimeBinding
    private val numImages = 10
    private lateinit var timePicker1: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        timePicker1 = binding.timePicker1

        timePicker1.setIs24HourView(true)
        timePicker1.setOnTimeChangedListener { _, hourOfDay, minute ->
            val animSlideInUp: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
            val animSlideOutDown: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)

            val hour1 = binding.textView1
            val hour2 = binding.textView2
            val minute1 = binding.textView3
            val minute2 = binding.textView4
            hour1.startAnimation(animSlideOutDown)
            hour2.startAnimation(animSlideOutDown)
            minute1.startAnimation(animSlideOutDown)
            minute2.startAnimation(animSlideOutDown)

            animSlideOutDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    hour1.text = String.format("%02d", hourOfDay / 10)
                    hour2.text = String.format("%02d", hourOfDay % 10)
                    minute1.text = String.format("%02d", minute / 10)
                    minute2.text = String.format("%02d", minute % 10)

                    hour1.startAnimation(animSlideInUp)
                    hour2.startAnimation(animSlideInUp)
                    minute1.startAnimation(animSlideInUp)
                    minute2.startAnimation(animSlideInUp)
                }
            })

            hour1.startAnimation(animSlideOutDown)
            hour2.startAnimation(animSlideOutDown)
            minute1.startAnimation(animSlideOutDown)
            minute2.startAnimation(animSlideOutDown)
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
    }
}