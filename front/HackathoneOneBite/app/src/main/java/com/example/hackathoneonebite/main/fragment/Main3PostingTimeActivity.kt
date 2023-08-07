package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingTimeBinding

class Main3PostingTimeActivity : AppCompatActivity() {

    lateinit var binding : ActivityMain3PostingTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        // UI에 데이터 표시
        binding.selectName.text = "$selectedName"

        if (receivedPost != null) {
            // Post 객체의 정보 받아서 처리
        }

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}