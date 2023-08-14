package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRelaySearchBinding
import okhttp3.MultipartBody

class Main3PostingRelaySearchActivity : AppCompatActivity() {

    private lateinit var adapter: AdapterMain3PostingRelaySearch
    lateinit var binding:ActivityMain3PostingRelaySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingRelaySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedIntent = intent
        val receivedPost = receivedIntent.getSerializableExtra("post_data") as? Post
        val imgPartArray = Array(4) { 0 }
        val imageByteArrays = ArrayList<ByteArray>()


        for (i in 0 until 4) {
            val byteArray = receivedIntent.getByteArrayExtra("imageByteArrays$i")
            if (byteArray != null) {
                imageByteArrays.add(byteArray)
            }
        }
        Log.d("new 받음",imageByteArrays.toString())

        val leftArrow = findViewById<ImageView>(R.id.leftArrow)
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        adapter = AdapterMain3PostingRelaySearch()
        binding.nameRecyclerView.adapter = adapter
        binding.nameRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnNameClickListener(object : AdapterMain3PostingRelaySearch.OnNameClickListener {
            override fun onNameClick(name: String) {

                val nextIntent = Intent(this@Main3PostingRelaySearchActivity, Main3PostingTimeActivity::class.java)
                nextIntent.putExtra("selected_name", name)
                nextIntent.putExtra("post_data", receivedPost)
                nextIntent.putExtra("imagePartSize", imgPartArray.size)

                for (i in 0 until imageByteArrays.size) {
                    nextIntent.putExtra("imageByteArrays$i", imageByteArrays[i]) // Pass individual byte arrays to the next screen
                    Log.d("new 보냄 ", imageByteArrays[i].toString())
                }

                startActivity(nextIntent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })

        binding.searchEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                adapter.clearData() // 포커스가 없으면 RecyclerView에 표시되는 이름을 모두 지움
            }
        }


        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    adapter.clearData() // 텍스트가 비어있을 경우 RecyclerView에 표시되는 이름을 모두 지움
                } else {
                    // TODO: 데베 데이터를 가져와서 여기서 나오게 해야함
                    val nameList = mutableListOf<String>()

                    for (i in 1..100) {
                        nameList.add("Name $i")
                    }
                    adapter.setData(nameList)
                    adapter.filter.filter(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
}
