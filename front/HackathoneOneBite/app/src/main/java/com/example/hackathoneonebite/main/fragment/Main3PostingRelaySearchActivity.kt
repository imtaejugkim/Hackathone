package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRelaySearchBinding

class Main3PostingRelaySearchActivity : AppCompatActivity() {

    private lateinit var adapter: AdapterMain3PostingRelaySearch
    lateinit var binding:ActivityMain3PostingRelaySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingRelaySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val rightArrow = binding.rightArrow
        rightArrow.setOnClickListener {
            val selectedName = binding.searchEdit.text.toString()

            val nextIntent = Intent(this, Main3PostingTimeActivity::class.java)
            nextIntent.putExtra("selected_name", selectedName)
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
        }

        adapter = AdapterMain3PostingRelaySearch()
        binding.nameRecyclerView.adapter = adapter
        binding.nameRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // TODO: 데베 데이터를 가져와서 여기서 나오게 해야함
        val nameList = mutableListOf<String>()

        for (i in 1..100) {
            nameList.add("Name $i")
        }
        adapter.setData(nameList)
    }
}
