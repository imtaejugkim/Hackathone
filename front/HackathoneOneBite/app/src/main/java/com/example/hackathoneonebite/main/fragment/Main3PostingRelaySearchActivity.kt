package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R

class Main3PostingRelaySearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_relay_search)

        val receivedIntent = intent
        val receivedPost = receivedIntent.getSerializableExtra("post_data") as? Post

        if (receivedPost != null) {
            Log.d("PostDebug", "Images: ${receivedPost.imgArray.joinToString()}")
            Log.d("PostDebug", "ID: ${receivedPost.id}")
            Log.d("PostDebug", "Like Count: ${receivedPost.likeCount}")
            Log.d("PostDebug", "Date: ${receivedPost.date}")
            Log.d("PostDebug", "Message: ${receivedPost.message}")
            Log.d("PostDebug", "Frame: ${receivedPost.frame}")
            Log.d("PostDebug", "Is Flipped: ${receivedPost.isFliped}")
        }
    }
}