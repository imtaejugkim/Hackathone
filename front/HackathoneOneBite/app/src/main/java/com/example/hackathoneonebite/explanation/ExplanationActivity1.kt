package com.example.hackathoneonebite.explanation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityExplanation1Binding

class ExplanationActivity1 : AppCompatActivity() {
    lateinit var binding: ActivityExplanation1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplanation1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rightTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}