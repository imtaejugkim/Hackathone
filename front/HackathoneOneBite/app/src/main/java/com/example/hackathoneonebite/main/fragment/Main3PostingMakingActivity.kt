package com.example.hackathoneonebite.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.RetrofitBuilder
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.time.LocalDateTime
import retrofit2.Callback
import retrofit2.Response


class Main3PostingMakingActivity : AppCompatActivity() {

    private lateinit var images: Array<String>
    private var currentLayoutId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_making)

        images = Array(4) { "" }

        // Intent에서 클릭된 레이아웃의 ID를 가져옴
        currentLayoutId = intent.getIntExtra("layout_id", 0)

        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        postImageLayoutFilm.visibility = View.INVISIBLE
        postImageLayout1.visibility = View.INVISIBLE
        postImageLayout2.visibility = View.INVISIBLE

        when (currentLayoutId) {
            0 -> {
                postImageLayoutFilm.visibility = View.VISIBLE
                contentFrameFilm()
            }
            1 -> {
                postImageLayout1.visibility = View.VISIBLE
                contentFrame1()
            }
            2 -> {
                postImageLayout2.visibility = View.VISIBLE
                contentFrame2()
            }
        }

        val leftArrow = findViewById<ImageView>(R.id.leftArrow)
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val relayButton = findViewById<Button>(R.id.relayButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)

        updateButtonsVisibility()

        relayButton.setOnClickListener {
            val message = "백엔드야 메세지 받아라"
            val post = Post(images, 0, "", 0, LocalDateTime.now(), message,false)

            val intent = Intent(this@Main3PostingMakingActivity, Main3PostingRelaySearchActivity::class.java)
            intent.putExtra("post_data", post)
            startActivity(intent)

        }

        uploadButton.setOnClickListener {
            val message = "백엔드야 메세지 받아라"
            val post = Post(images, 0, "", 0, LocalDateTime.now(), message,false)

            sendPost(post)
        }

    }

    private fun sendPost(post : Post) {
        val call = RetrofitBuilder.api.getPostResponse(post)
        call.enqueue(object : Callback<Post> { // 비동기 방식 통신 메소드
            override fun onFailure(call: Call<Post>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("PostDebug", "Images: ${post.imgArray.joinToString()}")
                    Log.d("PostDebug", "ID: ${post.id}")
                    Log.d("PostDebug", "Like Count: ${post.likeCount}")
                    Log.d("PostDebug", "Date: ${post.date}")
                    Log.d("PostDebug", "Message: ${post.message}")
                    Log.d("PostDebug", "Theme: ${post.theme}")
                    Log.d("PostDebug", "Is Flipped: ${post.isFliped}")
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingMakingActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingMakingActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingMakingActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateButtonsVisibility() {
        val relayButton = findViewById<Button>(R.id.relayButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val disabledButton = findViewById<Button>(R.id.unActiveButton)

        when (images.count { it.isNotBlank() }) {
            0 -> {
                relayButton.visibility = View.GONE
                uploadButton.visibility = View.GONE
                disabledButton.visibility = View.VISIBLE
            }
            in 1..3 -> {
                relayButton.visibility = View.VISIBLE
                uploadButton.visibility = View.GONE
                disabledButton.visibility = View.GONE
            }
            4 -> {
                relayButton.visibility = View.GONE
                uploadButton.visibility = View.VISIBLE
                disabledButton.visibility = View.GONE
            }
        }
    }



    private fun contentFrameFilm() {
        val img1 = findViewById<ImageView>(R.id.imageView1)
        val img2 = findViewById<ImageView>(R.id.imageView2)
        val img3 = findViewById<ImageView>(R.id.imageView3)
        val img4 = findViewById<ImageView>(R.id.imageView4)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                images[index] = true.toString()

            }
        }
    }

    private fun contentFrame1() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame1)
        val img2 = findViewById<ImageView>(R.id.imageView2frame1)
        val img3 = findViewById<ImageView>(R.id.imageView3frame1)
        val img4 = findViewById<ImageView>(R.id.imageView4frame1)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                images[index] = true.toString()

            }
        }
    }

    private fun contentFrame2() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame2)
        val img2 = findViewById<ImageView>(R.id.imageView2frame2)
        val img3 = findViewById<ImageView>(R.id.imageView3frame2)
        val img4 = findViewById<ImageView>(R.id.imageView4frame2)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                images[index] = true.toString()

            }
        }
    }

    companion object {
        const val REQUEST_CODE_SELECT_IMAGE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImagePath = data?.getStringExtra("selected_image_path")
            val contentsId = data?.getIntExtra("contents_id", 2) ?: 2
            if (selectedImagePath != null) {
                images[contentsId] = selectedImagePath
                val selectedImageView = findImageViewForCurrentContentFrame(contentsId, currentLayoutId)
                Glide.with(this)
                    .load(selectedImagePath)
                    .into(selectedImageView)

                updateButtonsVisibility()
            }
        }
    }

    private fun findImageViewForCurrentContentFrame(contentsId: Int, layoutId: Int): ImageView {

        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        when (layoutId) {
            0 -> {
                // LayoutFilm의 경우
                return when (contentsId) {
                    0 -> postImageLayoutFilm.findViewById(R.id.imageView1)
                    1 -> postImageLayoutFilm.findViewById(R.id.imageView2)
                    2 -> postImageLayoutFilm.findViewById(R.id.imageView3)
                    3 -> postImageLayoutFilm.findViewById(R.id.imageView4)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            1 -> {
                // Layout1의 경우
                return when (contentsId) {
                    0 -> postImageLayout1.findViewById(R.id.imageView1frame1)
                    1 -> postImageLayout1.findViewById(R.id.imageView2frame1)
                    2 -> postImageLayout1.findViewById(R.id.imageView3frame1)
                    3 -> postImageLayout1.findViewById(R.id.imageView4frame1)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            2 -> {
                // Layout2의 경우
                return when (contentsId) {
                    0 -> postImageLayout2.findViewById(R.id.imageView1frame2)
                    1 -> postImageLayout2.findViewById(R.id.imageView2frame2)
                    2 -> postImageLayout2.findViewById(R.id.imageView3frame2)
                    3 -> postImageLayout2.findViewById(R.id.imageView4frame2)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            else -> throw IllegalArgumentException("Invalid layoutId")
        }
    }
}