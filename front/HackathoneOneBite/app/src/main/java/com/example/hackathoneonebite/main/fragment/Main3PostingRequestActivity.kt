package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRequestBinding

class Main3PostingRequestActivity : AppCompatActivity() {

    lateinit var binding : ActivityMain3PostingRequestBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        binding.selectName.text = "$selectedName 님에게 릴레이 수락 요청 보내시겠습니까"

        val cancelButton = binding.cancelBtn
        val okBtn = binding.okBtn
        val leftArrow = binding.leftArrow

        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        cancelButton.setOnClickListener {
            val nextIntent = Intent(this, Main3PostingRelaySearchActivity::class.java)
            nextIntent.putExtra("post_data",receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        okBtn.setOnClickListener {
            val nextIntent = Intent(this, Main3PostingUploadActivity::class.java)
            nextIntent.putExtra("selected_name", selectedName)
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }
}