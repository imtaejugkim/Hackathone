package com.example.hackathoneonebite

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.Data.User
import com.example.hackathoneonebite.api.LoginCheckEmailExistRequest
import com.example.hackathoneonebite.api.LoginCheckEmailExistResponse
import com.example.hackathoneonebite.api.Main1LoadPostRequest
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityTestLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }
    @SuppressLint("ResourceType")
    fun init() {
        binding.loginButton.setOnClickListener {
            val inputStream = this.resources.openRawResource(R.drawable.cd_outer_playing)
            val byteArray = inputStream.readBytes()
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
            val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), "1")
            val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), "tae0803")
            val imagePart = MultipartBody.Part.createFormData("image", "your_image_name.jpg", requestFile)
            val imagePart2 = MultipartBody.Part.createFormData("image", "your_image_name2.jpg", requestFile)
            val imagePart3 = MultipartBody.Part.createFormData("image", "your_image_name3.jpg", requestFile)
            val imagePart4 = MultipartBody.Part.createFormData("image", "your_image_name4.jpg", requestFile)

            val imageParts = ArrayList<MultipartBody.Part>()
            imageParts.add(imagePart)
            imageParts.add(imagePart2)
            imageParts.add(imagePart3)
            imageParts.add(imagePart4)
            Login(imageParts, themePart, idPart)
        }
        binding.loadImage.setOnClickListener {
            loadPost(1652)
        }
    }

    fun loadPost(postId: Long){
        val call = RetrofitBuilder.api.main1LoadPostRequest(postId)
        call.enqueue(object : Callback<Main1LoadPostResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main1LoadPostResponse>,
                response: Response<Main1LoadPostResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    Log.d("RESPONSE: ", "success")
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "${userResponse?.date}")
                    binding.textView.text = userResponse?.text
                    Log.d("RESPONSE: ", userResponse!!.images[0])

                    for(i in 0..3) {
                        Glide.with(this@TestLoginActivity)
                            .load(
                                "http://221.146.39.177:8081/" + userResponse!!.images[i].substring(
                                    userResponse!!.images[i].lastIndexOf("\\") + 1
                                )
                            )
                            .into(binding.image1)
                        Log.d(
                            "RESPONSE: ",
                            userResponse!!.images[i].substring(
                                userResponse!!.images[i].lastIndexOf("\\") + 1
                            )
                        )
                    }
                    Toast.makeText(this@TestLoginActivity,userResponse?.date,Toast.LENGTH_LONG).show()
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@TestLoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@TestLoginActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@TestLoginActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<Main1LoadPostResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    fun Login(image: ArrayList<MultipartBody.Part>, theme: RequestBody, userId: RequestBody){
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId)
        call.enqueue(object : Callback<Main1LoadPostResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main1LoadPostResponse>,
                response: Response<Main1LoadPostResponse>
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
                            Toast.makeText(this@TestLoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@TestLoginActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@TestLoginActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Main1LoadPostResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}