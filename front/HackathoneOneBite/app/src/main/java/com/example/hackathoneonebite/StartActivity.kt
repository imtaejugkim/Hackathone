package com.example.hackathoneonebite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.hackathoneonebite.Data.User
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityStartBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartActivity : ComponentActivity() {
    lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }
    fun init() {
        binding.loginBtn.setOnClickListener {
            val i = Intent(this@StartActivity, MainFrameActivity::class.java)
            startActivity(i)
/*            val user = User()
            user.id = binding.userID.text.toString()
            user.pw = binding.userPW.text.toString()

            Log.d("Login Button Clicked", "ID:" + user.id + " / PW:" + user.pw)
            Login(user)*/
        }
    }

    /*fun Login(user: User){
        val call = RetrofitBuilder.api.getLoginResponse(user)
        call.enqueue(object : Callback<User> { // 비동기 방식 통신 메소드
            override fun onResponse( // 통신에 성공한 경우
                call: Call<User>,
                response: Response<User>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "ID: ${userResponse?.id}, Name: ${userResponse?.pw}")
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@StartActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@StartActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@StartActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }*/
}
