package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.MyApplication.Companion.imageByteArrays
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3RelaySearchResponse
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.NotificationLoadResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRelaySearchBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main3PostingRelaySearchActivity : AppCompatActivity() {

    private lateinit var adapter: AdapterMain3PostingRelaySearch
    private  var nameList = mutableListOf<String>()
    lateinit var binding:ActivityMain3PostingRelaySearchBinding
    var id : Long = 0
    var selectedId : Long = 0
    var userId : String = ""
    var requestNumber : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingRelaySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedIntent = intent
        id = receivedIntent.getLongExtra("id", 0)
        userId = receivedIntent.getStringExtra("userId") + ""
        requestNumber = receivedIntent.getIntExtra("requestNumber",-1)
        val receivedPost = receivedIntent.getSerializableExtra("post_data") as? Post
        val imgPartArray = Array(4) { 0 }


        val leftArrow = findViewById<ImageView>(R.id.leftArrow)
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        adapter = AdapterMain3PostingRelaySearch()
        binding.nameRecyclerView.adapter = adapter
        binding.nameRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnNameClickListener(object : AdapterMain3PostingRelaySearch.OnNameClickListener {
            override fun onNameClick(name: String) {


                val parts = name.split("(", ")","(",")")
                Log.d("parts",parts.toString())
                val selectedName = parts[0].trim()
                val selectedUserId = parts[1].trim()
                val selectedId = parts[3].trim().toLong()

                Log.d("selectedUserId",selectedUserId)
                Log.d("selected",selectedId.toString())

                if (userId == selectedUserId) {
                    Toast.makeText(
                        this@Main3PostingRelaySearchActivity,
                        "선택할 수 없습니다",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    val nextIntent = Intent(
                        this@Main3PostingRelaySearchActivity,
                        Main3PostingTimeActivity::class.java
                    )
                    nextIntent.putExtra("selected_name", selectedName)
                    nextIntent.putExtra("post_data", receivedPost)
                    nextIntent.putExtra("imagePartSize", imgPartArray.size)
                    nextIntent.putExtra("selectedUserId", selectedUserId)
                    nextIntent.putExtra("selectedId",selectedId)
                    Log.d("selectedUserId", selectedUserId)
                    nextIntent.putExtra("id", id)
                    nextIntent.putExtra("requestNumber",requestNumber)
                    nextIntent.putExtra("userId",userId)

                    startActivity(nextIntent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
        })

        binding.searchEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                adapter.clearData() // 포커스가 없으면 RecyclerView에 표시되는 이름을 모두 지움
            }
        }


        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    adapter.clearData() // 텍스트가 비어있을 경우 RecyclerView에 표시되는 이름을 모두 지움
                } else {
                    Search(id,binding.searchEdit.text.toString())
                    adapter.setData(nameList)
                    adapter.filter.filter(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    fun Search(id:Long, q : String){
        val call = RetrofitBuilder.api.main3LoadUserRequest(id, q)
        call.enqueue(object : Callback<List<Main3RelaySearchResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<Main3RelaySearchResponse>>,
                response: Response<List<Main3RelaySearchResponse>>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    if (userResponse != null) {
                        val nameIdList = userResponse.map { "${it.username}(${it.userId})(${it.id})" }.toMutableList()
                        Log.d("nameIdList",nameIdList.toString())
                        adapter.setData(nameIdList)
                        adapter.notifyDataSetChanged()
                    }
                } else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingRelaySearchActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingRelaySearchActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingRelaySearchActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Main3RelaySearchResponse>>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)

            }
        })
    }

}
