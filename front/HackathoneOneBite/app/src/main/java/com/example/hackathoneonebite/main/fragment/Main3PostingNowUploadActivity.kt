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
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.MyApplication
import com.example.hackathoneonebite.MyApplication.Companion.imageByteArrays
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityMain3PostingNowUploadBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    val baseUrl: String = "http://203.252.139.231:8080/"
    private lateinit var images: Array<ByteArray> // image 변환 전 byteArray들
    var id : Long = 0

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
        id = receivedIntent.getLongExtra("id",id)

        loadProfileInfoMine(id)

        var theme = receivedPost?.theme
        var userId = receivedPost?.userId

        binding.userId.text = userId.toString()

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

            var imageByteArrayIndex = 0 // imageByteArrays 리스트의 인덱스
            val imageParts = ArrayList<MultipartBody.Part>()
            images = Array(4) { ByteArray(0) }

            for (i in 0..3) {
                if (receivedPost!!.imgArray[i] == "true") {
                    if (imageByteArrayIndex < imageByteArrays.size) {
                        images[i] = imageByteArrays[imageByteArrayIndex]
                        Log.d("images[$i]",images[i].toString())
                        Log.d("인덱스는?",imageByteArrayIndex.toString())
                        imageByteArrayIndex++
                        val requestFile =
                            RequestBody.create("image/*".toMediaTypeOrNull(), images[i])
                        imageParts.add(MultipartBody.Part.createFormData(
                            "image",
                            "image$i.jpg",
                            requestFile
                        ))
                    }
                }
            }

            val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), theme.toString())
            val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
            val musicPart = RequestBody.create("text/plain".toMediaTypeOrNull(), musicNum.toString())
            val message = RequestBody.create("text/plain".toMediaTypeOrNull(), message)

            Upload(imageParts, themePart, idPart, musicPart, message)
            Log.d("이미지",imageParts.toString())
            Log.d("테마",theme.toString())
            Log.d("유저 아이디",userId.toString())
            Log.d("음악",musicNum.toString())
            Log.d("글",message.toString())

            MyApplication.imageByteArrays.clear()

            val intent = Intent(this@Main3PostingNowUploadActivity, MainFrameActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("id",id)
            startActivity(intent)
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

    fun Upload(image: ArrayList<MultipartBody.Part>, theme: RequestBody, userId: RequestBody, musicNum: RequestBody, message: RequestBody){
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId, musicNum , message)
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

    private fun loadProfileInfoMine(id: Long) {
        Log.d("Mine", "id : " + id.toString())
        loadProfileInfoRequest(id, id)
        Log.d("나 혼자",id.toString())
    }

    private fun loadProfileInfoRequest(targetId: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadProfileInfo(targetId, currentId)
        call.enqueue(object : retrofit2.Callback<Main5LoadProfileInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main5LoadProfileInfoResponse>,
                response: retrofit2.Response<Main5LoadProfileInfoResponse>
            ) {
                Log.e("MAIN5PROFILE: LOAD PROFILE INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val data_profile = response.body()
                    changeProfileImageMine(baseUrl + data_profile?.profileImageUrl)
                    Log.d("이미지 url 내거",data_profile?.profileImageUrl.toString())
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingNowUploadActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO3", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingNowUploadActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingNowUploadActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<Main5LoadProfileInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD PROFILE INFO4", t.localizedMessage)
            }
        })
    }

    private fun changeProfileImageMine(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.relayGiverProfile)
    }
}
