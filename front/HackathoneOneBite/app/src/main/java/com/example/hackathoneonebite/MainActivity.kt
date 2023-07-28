package com.example.hackathoneonebite

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.hackathoneonebite.databinding.MainActivityBinding

class MainActivity : ComponentActivity() {
    lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)

        init()
    }
    fun init() {
        binding.loginBtn.setOnClickListener {
            var ID = binding.userID.text
        }
    }
}