package com.example.hackathoneonebite

import com.example.hackathoneonebite.R
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.databinding.ActivitySplashBinding
import com.example.hackathoneonebite.main.MainFrameActivity


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HackathoneOneBite)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val animation: Animation = AnimationUtils.loadAnimation(this,R.anim.splash)
        binding.splashLogo.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()  // 현재 액티비티 종료
        }, 1100L)
    }
}