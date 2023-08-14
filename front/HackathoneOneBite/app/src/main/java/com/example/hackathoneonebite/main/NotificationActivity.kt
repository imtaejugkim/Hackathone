package com.example.hackathoneonebite.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.databinding.ActivityNotificationBinding
import com.example.hackathoneonebite.databinding.ActivitySignInBinding

class NotificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }


    private fun init(){

    }
}
