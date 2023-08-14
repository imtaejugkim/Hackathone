package com.example.hackathoneonebite.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    var id: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile)

        id = intent.getLongExtra("id", 0)
    }
}