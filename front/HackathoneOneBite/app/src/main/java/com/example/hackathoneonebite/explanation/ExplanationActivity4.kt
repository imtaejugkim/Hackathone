package com.example.hackathoneonebite.explanation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityExplanation1Binding
import com.example.hackathoneonebite.databinding.ActivityExplanation4Binding

class ExplanationActivity4 : AppCompatActivity() {
    lateinit var binding: ActivityExplanation4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplanation4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_and_clip)
        binding.arrowImageView.startAnimation(animation)

        binding.rightTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity5::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        binding.leftTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}