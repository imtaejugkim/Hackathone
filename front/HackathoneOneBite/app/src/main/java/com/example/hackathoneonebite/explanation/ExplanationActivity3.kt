package com.example.hackathoneonebite.explanation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityExplanation1Binding
import com.example.hackathoneonebite.databinding.ActivityExplanation3Binding

class ExplanationActivity3 : AppCompatActivity() {
    lateinit var binding: ActivityExplanation3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplanation3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rightTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity4::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        binding.leftTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}