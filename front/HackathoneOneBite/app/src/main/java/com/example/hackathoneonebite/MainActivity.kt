package com.example.hackathoneonebite

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.hackathoneonebite.Data.User
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.MainActivityBinding
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext


class MainActivity : ComponentActivity() {
    lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }
    fun init() {
        binding.loginBtn.setOnClickListener {
            val user = User()
            user.id = binding.userID.text.toString()
            user.pw = binding.userPW.text.toString()

            Log.d("Login Button Clicked", "ID:" + user.id + " / PW:" + user.pw)
            Login(user)
        }
    }

    fun Login(user: User){
        val call = RetrofitBuilder.api.getLoginResponse(user)
        call.enqueue(object : Callback<String> { // 비동기 방식 통신 메소드
            override fun onResponse( // 통신에 성공한 경우
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    Log.d("RESPONSE: ", response.body().toString())
                }else{
                    // 통신 성공 but 응답 실패
                    Log.d("RESPONSE", "FAILURE:"+ response.errorBody().toString())
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@MainActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}
