package com.example.hackathoneonebite.explanation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.StartActivity
import com.example.hackathoneonebite.databinding.ActivityExplanation8Binding

class ExplanationActivity8 : AppCompatActivity() {
    lateinit var binding: ActivityExplanation8Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplanation8Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
        binding.leftTouchView.setOnClickListener {
            val intent = Intent(this, ExplanationActivity7::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}