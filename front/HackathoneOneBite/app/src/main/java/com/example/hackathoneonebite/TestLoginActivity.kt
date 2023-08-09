package com.example.hackathoneonebite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.api.LoginCheckEmailExistRequest
import com.example.hackathoneonebite.api.LoginCheckEmailExistResponse
import com.example.hackathoneonebite.api.Main1LoadPostRequest
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityTestLoginBinding
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
    private fun init() {
        binding.loginButton.setOnClickListener {
            val request = LoginCheckEmailExistRequest(binding.userID.text.toString())
            checkEmailExist(request)
        }
    }

    fun checkEmailExist(request: LoginCheckEmailExistRequest){
        val call = RetrofitBuilder.api.loginCheckEmailExistRequest(request)
        call.enqueue(object : Callback<LoginCheckEmailExistResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<LoginCheckEmailExistResponse>,
                response: Response<LoginCheckEmailExistResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    Log.d("RESPONSE: ", "success")
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "${userResponse?.isExist.toString()}")
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

            override fun onFailure(call: Call<LoginCheckEmailExistResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}