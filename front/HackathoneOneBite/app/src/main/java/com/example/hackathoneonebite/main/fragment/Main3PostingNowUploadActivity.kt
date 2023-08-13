package com.example.hackathoneonebite.main.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityMain3PostingNowUploadBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main3PostingNowUploadActivity : AppCompatActivity(),
    AdapterMain3PostingUpload.OnButtonClickListener {

    private var isRotating = false
    private var isPlaying = false
    private var rotationAnimator: ValueAnimator? = null
    private var mediaPlayer: MediaPlayer? = null
    private var selectedMusicPosition = 0
    private var musicIsExist = false

    lateinit var binding: ActivityMain3PostingNowUploadBinding
    private val imageResources = arrayOf(
        R.drawable.cd_music0,
        R.drawable.cd_music1,
        R.drawable.cd_music2,
        R.drawable.cd_music3,
        R.drawable.cd_music4,
        R.drawable.cd_music5,
        R.drawable.cd_music6,
    )

    private val musicResources = arrayOf(
        R.raw.music0_link_jim_yosef,
        R.raw.music1_on_and_on_cartoon,
        R.raw.music2_heroes_tonight_janji,
        R.raw.music3_my_heart_different_heaven_ehde,
        R.raw.music4_mortals_warriyo,
        R.raw.music5_sky_high_elektronomia,
        R.raw.music6_fearless_lost_sky,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingNowUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val receivedIntent = intent
        val selectedName = receivedIntent.getStringExtra("selected_name")
        val receivedPost = receivedIntent.getSerializableExtra("post_data") as? Post

        Log.d("post",receivedPost.toString())

        val imageSize = intent.getIntExtra("imagePartSize", 0)
        //Log.d("imagePartSize",imageSize.toString())

        /*if (receivedPost != null) {
            Log.d("PostDebug", "ID: ${receivedPost.userId}")
            Log.d("PostDebug", "Like Count: ${receivedPost.likeCount}")
            Log.d("PostDebug", "Date: ${receivedPost.date}")
            Log.d("PostDebug", "Message: ${receivedPost.message}")
            Log.d("PostDebug", "Theme: ${receivedPost.theme}")
            Log.d("PostDebug", "Is Flipped: ${receivedPost.isFliped}")
        }*/

        val imageParts = ArrayList<MultipartBody.Part>()
        for (i in 0 until imageSize) {
            val partName = intent.getStringExtra("imagePart$i")
            val part = partName?.let { MultipartBody.Part.createFormData(it, null.toString()) }
            if (part != null) {
                imageParts.add(part)
                Log.d("이미지 파트 들어옴", part.toString())
            }
        }
        Log.d("이미지 파트",imageParts.toString())

        val imgArray = receivedPost?.imgArray
        val theme = receivedPost?.theme
        val userId = receivedPost?.userId
        val likeCount = receivedPost?.likeCount
        val date = receivedPost?.date
        val isFliped = receivedPost?.isFliped
        val likeClicked = receivedPost?.likeClicked
        val participantUserIds = receivedPost?.participantUserIds

        binding.relayName.text = "$selectedName"

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = AdapterMain3PostingNowUpload(this)
        recyclerView.adapter = adapter

        val editText = binding.editText
        editText.background = null;
        val message = editText.text.toString()


        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val rightArrow = binding.rightArrow
        rightArrow.setOnClickListener {
            //val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), theme.toString())
            val musicNum = if(musicIsExist){
                selectedMusicPosition
            }else{ -1 }

            Upload(imageParts, theme!!, userId!!, musicNum, message)
            val intent = Intent(this@Main3PostingNowUploadActivity, MainFrameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            Log.d("이미지",imageParts.toString())
            Log.d("테마",theme.toString())
            Log.d("유저 아이디",userId.toString())
            Log.d("음악",musicNum.toString())
            Log.d("글",message.toString())
        }

        binding.playButton.setOnClickListener {
            if (isRotating) {
                stopRotation()
                stopMusic()
                musicIsExist = false
            } else {
                startRotation()
                startMusic(selectedMusicPosition)
                musicIsExist = true
            }
        }
    }

    fun Upload(image: ArrayList<MultipartBody.Part>, theme: Int, userId: String, musicNum: Int?, message: String?){
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId, musicNum , message!!)
        call.enqueue(object : Callback<Main3UploadPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main3UploadPostIsComplete>,
                response: Response<Main3UploadPostIsComplete>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "Success")
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingNowUploadActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingNowUploadActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingNowUploadActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Main3UploadPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    override fun onButtonClicked(position: Int) {
        selectedMusicPosition = position

        binding.mp3Song.visibility = View.VISIBLE
        binding.cdImageView.setImageResource(imageResources[position])

        if(!musicIsExist) {
            musicIsExist
        }
        else !musicIsExist
    }

    private fun startRotation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                binding.cdImageView.rotation = animatedValue
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    isRotating = true
                }
            })
        }
        rotationAnimator?.start()
    }

    private fun stopRotation() {
        rotationAnimator?.cancel()
        binding.cdImageView.rotation = 0f
    }

    private fun startMusic(selectedPosition : Int) {
        if (!isPlaying) {
            mediaPlayer = MediaPlayer.create(this, musicResources[selectedPosition])
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun stopMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                reset()
                release()
                this@Main3PostingNowUploadActivity.isPlaying = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
