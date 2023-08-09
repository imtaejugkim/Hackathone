package com.example.hackathoneonebite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.api.LoginCheckEmailExistRequest
import com.example.hackathoneonebite.api.LoginCheckEmailExistResponse
import com.example.hackathoneonebite.api.LoginSignInRequest
import com.example.hackathoneonebite.api.LoginSignInResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivitySignInBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    private fun init() {
        binding.email.text =  intent.getStringExtra("email")
        binding.username.text =  intent.getStringExtra("displayName")
        binding.singInBtn.setOnClickListener {
            val request = LoginSignInRequest(binding.username.text.toString(), binding.userId.text.toString(), binding.password.text.toString(), binding.email.text.toString())
            signIn(request)
        }
    }

    private fun movePage() {
        val nextIntent = Intent(this, MainFrameActivity::class.java)
        startActivity(nextIntent)
    }

    fun signIn(request: LoginSignInRequest){
        val call = RetrofitBuilder.api.loginSignInRequest(request)
        call.enqueue(object : Callback<LoginSignInResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<LoginSignInResponse>,
                response: Response<LoginSignInResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    Log.d("RESPONSE: ", "success")
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "${userResponse?.isSuccess.toString()}")
                    if(userResponse?.isSuccess!!) {
                        movePage()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@SignInActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@SignInActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignInActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginSignInResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}