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
        binding.username.setText(intent.getStringExtra("displayName"))
        binding.singInBtn.setOnClickListener {
            val request = LoginSignInRequest(binding.username.text.toString(), binding.userId.text.toString(), binding.email.text.toString())
            signIn(request)
        }
    }

    private fun movePage(id: Long) {
        val nextIntent = Intent(this, MainFrameActivity::class.java)
        nextIntent.putExtra("id", id)
        nextIntent.putExtra("userId", binding.userId.text.toString())
        onBackPressedDispatcher.onBackPressed()
        startActivity(nextIntent)
    }

    fun handleSignInResponse(response: LoginSignInResponse) {
        if(response.isSuccess) { //가입 성공
            movePage(response.id)
            Log.d("SIGN IN: ", "회원가입 성공 로그인 합니다.")
        } else { //가입 실패
            if(response.id == -1L) { //아이디 중복
                Toast.makeText(this, "아이디가 존재합니다.", Toast.LENGTH_SHORT).show()
                Log.d("SIGN IN: ", "아이디가 존재합니다.")
                binding.userId.setText("")
                binding.userId.requestFocus()
            } else { //그 외의 문제 (response.id == 0)
                Toast.makeText(this, "가입이 완료되지 않았습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                Log.d("SIGN IN: ", "오류 발생")
            }
        }
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
                    if(userResponse != null) {
                        handleSignInResponse(userResponse)
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
                Toast.makeText(this@SignInActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}