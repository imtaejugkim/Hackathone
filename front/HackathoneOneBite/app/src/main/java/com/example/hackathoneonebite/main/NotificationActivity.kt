package com.example.hackathoneonebite.main

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.NotificationLoadResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityNotificationBinding
import com.example.hackathoneonebite.main.fragment.AdapterNotification
import com.example.hackathoneonebite.main.fragment.NotificationItem
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Types.NULL

class NotificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("userId")

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        loadNotificationRequest(userId!!)
        Log.d("userId",userId.toString())
    }


    private fun init(
        id: Long?,
        message: String?,
        createdAt: String?,
        userName: String?,
        remainingTime: String?,
        postId: Long?
    ) {
        val recyclerView = binding.recyclerView
        val adapter = AdapterNotification()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        var time = ""
        if(remainingTime != null){
            time = remainingTime
        }
        adapter.addItem(NotificationItem(id, userName, message, time, postId))

    }


    private fun loadNotificationRequest(userId : String) {
        val call = RetrofitBuilder.api.notificationLoadRequest(userId)
        call.enqueue(object : Callback<NotificationLoadResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<NotificationLoadResponse>,
                response: Response<NotificationLoadResponse>
            ) {
                Log.e("Notification: LOAD POST INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    val id = userResponse?.id
                    val message = userResponse?.message
                    val createdAt = userResponse?.createdAt
                    val userName = userResponse?.userName
                    val remainingTime = userResponse?.remainingTime
                    val postId = userResponse?.postId
                    init(id,message,createdAt,userName,remainingTime,postId)
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            //Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("Notification: LOAD POST INFO2", "Failed to parse error response: $errorBody")
                            //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

                            }
                    } else {
                        //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<NotificationLoadResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD POST INFO3", t.localizedMessage)

            }

        })
    }
}
