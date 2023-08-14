package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRequestBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime

class Main3PostingRequestActivity : AppCompatActivity() {

    lateinit var binding: ActivityMain3PostingRequestBinding
    private lateinit var images: Array<ByteArray> // image 변환 전 byteArray들


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post
        val selectedTime = intent.getIntExtra("selected_time", 0)
        val imageSize = intent.getIntExtra("imagePartSize", 0)
        val imageParts = arrayOfNulls<String>(4)
        val imagePartSize = intent.getIntExtra("imagePartSize", 0)
        val imageByteArrays = ArrayList<ByteArray>()

        var userId = receivedPost?.userId
        var message = receivedPost?.message
        var theme = receivedPost?.theme
        var musicNum = receivedPost?.musicNum


        for (i in 0 until 4) {
            val byteArray = intent.getByteArrayExtra("imageByteArrays$i")
            if (byteArray != null) {
                imageByteArrays.add(byteArray)
            }
        }
        Log.d("new 받음", imageByteArrays.toString())

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
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        okBtn.setOnClickListener {
            if (receivedPost!!.imgArray[0] == true.toString() &&
                receivedPost!!.imgArray[1] == true.toString() &&
                receivedPost!!.imgArray[2] == true.toString() &&
                receivedPost!!.imgArray[3] == true.toString()
            ) {
                val nextIntent = Intent(this, Main3PostingUploadActivity::class.java)
                nextIntent.putExtra("selected_name", selectedName)
                nextIntent.putExtra("post_data", receivedPost)
                // 배열의 각 원소 정보를 넘겨주고 로그 출력

                for (i in 0 until imageByteArrays.size) {
                    intent.putExtra(
                        "imageByteArrays$i",
                        imageByteArrays[i]
                    ) // Pass individual byte arrays to the next screen
                    Log.d("new 보냄 ", imageByteArrays[i].toString())
                }
                startActivity(nextIntent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            } else {
                var imageByteArrayIndex = 0 // imageByteArrays 리스트의 인덱스
                val imageParts = ArrayList<MultipartBody.Part>()
                images = Array(4) { ByteArray(0) }

                for (i in 0..3) {
                    if (receivedPost!!.imgArray[i] == "true") {
                        if (imageByteArrayIndex < imageByteArrays.size) {
                            images[i] = imageByteArrays[imageByteArrayIndex]
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
                val message = RequestBody.create("text/plain".toMediaTypeOrNull(), "hello")

                Upload(imageParts, themePart, idPart, musicPart, message)
                Log.d("theme",theme.toString())
                Log.d("userId",userId.toString())
                Log.d("message",message.toString())
                Log.d("musicNum",musicNum.toString())
                Log.d("image1",imageParts[0].toString())

            }

        }

    }

    fun Upload(image: ArrayList<MultipartBody.Part>, theme: RequestBody, userId: RequestBody, musicNum : RequestBody, message: RequestBody) {
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId, musicNum, message)
        call.enqueue(object : retrofit2.Callback<Main3UploadPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main3UploadPostIsComplete>,
                response: retrofit2.Response<Main3UploadPostIsComplete>
            ) {
                if (response.isSuccessful()) { // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "Success")
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(
                                this@Main3PostingRequestActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(
                                this@Main3PostingRequestActivity,
                                "오류가 발생했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Main3PostingRequestActivity,
                            "오류가 발생했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Main3UploadPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}

