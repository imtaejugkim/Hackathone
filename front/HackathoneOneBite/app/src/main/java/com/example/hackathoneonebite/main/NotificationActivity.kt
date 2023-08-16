package com.example.hackathoneonebite.main

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.NotificationLoadResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityNotificationBinding
import com.example.hackathoneonebite.main.fragment.AdapterMain3PostingRelaySearch
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
    private var notificationList = mutableListOf<NotificationItem>()
    lateinit var adapter: AdapterNotification



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("userId")
        val id = intent.getLongExtra("id",0)

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        adapter = AdapterNotification(notificationList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        loadNotificationRequest(id)
        Log.d("userId",userId.toString())
    }



    private fun loadNotificationRequest(userId : Long) {
        val call = RetrofitBuilder.api.notificationLoadRequest(userId)
        call.enqueue(object : Callback<List<NotificationLoadResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<NotificationLoadResponse>>,
                response: Response<List<NotificationLoadResponse>>
            ) {
                Log.e("Notification: LOAD POST INFO0", response.raw().request.url.toString())
                if (response.isSuccessful()) {
                    val userResponse = response.body()
                    notificationList.clear() // Clear the list before adding new items

                    val notificationItems = userResponse?.map {
                        NotificationItem(
                            it.id,
                            it.userId,
                            it.message,
                            it.createdAt,
                            it.userName,
                            it.remainingTime,
                            it.postId
                        )
                    }

                    if (notificationItems != null) {
                        notificationList.addAll(notificationItems)
                        adapter.notifyDataSetChanged() // Notify the adapter about the changes
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                        } catch (e: JSONException) {
                            Log.e("Notification: LOAD POST INFO2", "Failed to parse error response: $errorBody")

                            }
                    } else {
                        Toast.makeText(this@NotificationActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<NotificationLoadResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD POST INFO3", t.localizedMessage)

            }

        })
    }
}
