package com.example.hackathoneonebite

import android.content.Context
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
import com.example.hackathoneonebite.explanation.ExplanationActivity1
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
        //애니메이션
        val animation: Animation = AnimationUtils.loadAnimation(this,R.anim.splash)
        binding.splashLogo.startAnimation(animation)
        //앱이 처음 실행되는지 확인
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, ExplanationActivity1::class.java)
                startActivity(intent)
                finish()  // 현재 액티비티 종료
            }, 1100L)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                finish()  // 현재 액티비티 종료
            }, 1100L)
        }
    }
}