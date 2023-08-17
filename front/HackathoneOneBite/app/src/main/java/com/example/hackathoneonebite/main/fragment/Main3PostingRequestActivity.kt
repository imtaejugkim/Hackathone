package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.MyApplication
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3AddPostIsComplete
import com.example.hackathoneonebite.api.Main3RelayPostIsComplete
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityMain3PostingRequestBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
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
    val baseUrl: String = "http://203.252.139.231:8080/"
    var refreshingProfile: Boolean = false
    var id: Long = 0
    var selectedUserId : String = ""
    var selectedId : Long = 0
    var requestNumber : Int = -1
    var postId : Long = -1
    var userId : String = ""

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
        selectedUserId = intent.getStringExtra("selectedUserId")?:""
        postId = intent.getLongExtra("postId",0)
        id = intent.getLongExtra("id",0)
        selectedId = intent.getLongExtra("selectedId",0)
        requestNumber = intent.getIntExtra("requestNumber",-1)



        var userId = receivedPost?.userId
        var message = receivedPost?.message
        var theme = receivedPost?.theme
        var musicNum = receivedPost?.musicNum


        binding.selectName.text = "$selectedName 님에게 릴레이 수락 요청 보내시겠습니까"
        loadProfileInfoMine(id)
        loadProfileInfoTarget(id,selectedId)
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
            // 처음 만든 사람
            if(requestNumber==0){
                val nextIntent = Intent(this, Main3PostingUploadActivity::class.java)
                nextIntent.putExtra("selected_name", selectedName)
                nextIntent.putExtra("post_data", receivedPost)
                nextIntent.putExtra("id",id)

                startActivity(nextIntent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            // 두번째 만든 사람 부터
            else{
                var imageByteArrayIndex = 0 // imageByteArrays 리스트의 인덱스
                val imageParts = ArrayList<MultipartBody.Part>(4)
                images = Array(4) { ByteArray(0) }

                for (i in 0..3) {
                    Log.d("imageArray$i", receivedPost!!.imgArray[i])
                    if (receivedPost!!.imgArray[i] == "true") {
                        if (imageByteArrayIndex < MyApplication.imageByteArrays.size) {
                            images[i] = MyApplication.imageByteArrays[imageByteArrayIndex]
                            Log.d("images[$i]", images[i].toString())
                            Log.d("인덱스는?", imageByteArrayIndex.toString())
                            imageByteArrayIndex++
                            val requestFile =
                                RequestBody.create("image/*".toMediaTypeOrNull(), images[i])
                            imageParts.add(
                                MultipartBody.Part.createFormData(
                                    "image",
                                    "image$i.jpg",
                                    requestFile
                                )
                            )
                        }
                    }
                    /*else{
                        val emptyRequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
                        val emptyPart = MultipartBody.Part.createFormData("empty_part_name", "", emptyRequestBody)
                        imageParts.add(emptyPart)
                    }*/
                }

                val idPart =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), selectedId.toString())
                val postIdPart =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), postId.toString())
                val timePart =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), selectedTime.toString())
                val selectedIdPart =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), selectedUserId)

                AddPost(postIdPart, imageParts, idPart)
                relayRequest(postIdPart, selectedIdPart, timePart)

                MyApplication.imageByteArrays.clear()

                val nextIntent = Intent(this, MainFrameActivity::class.java)
                nextIntent.putExtra("userId",userId)
                nextIntent.putExtra("id",id)
                startActivity(nextIntent)
                onBackPressedDispatcher.onBackPressed()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
        }
    }

    //프로필 정보 호출
    private fun loadProfileInfoMine(id: Long) {
        Log.d("Mine", "id : " + id.toString())
        loadProfileInfoRequest(id, id)
        Log.d("나 혼자",id.toString())
    }

    private fun loadProfileInfoTarget(id:Long, selectedUserId:Long){
        Log.d("Target", "id : " + selectedUserId.toString())
        loadProfileInfoTargetRequest(selectedUserId, id)
        Log.d("id",id.toString())
        Log.d("selectedUser",selectedUserId.toString())
    }

    private fun loadProfileInfoRequest(targetId: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadProfileInfo(targetId, currentId)
        call.enqueue(object : retrofit2.Callback<Main5LoadProfileInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main5LoadProfileInfoResponse>,
                response: retrofit2.Response<Main5LoadProfileInfoResponse>
            ) {
                Log.e("MAIN5PROFILE: LOAD PROFILE INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                        val data_profile = response.body()
                        changeProfileImageMine(baseUrl + data_profile?.profileImageUrl)
                        Log.d("이미지 url 내거",data_profile?.profileImageUrl.toString())
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingRequestActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO3", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingRequestActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingRequestActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<Main5LoadProfileInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD PROFILE INFO4", t.localizedMessage)
            }
        })
    }

    private fun loadProfileInfoTargetRequest(targetId: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadProfileInfo(targetId, currentId)
        call.enqueue(object : retrofit2.Callback<Main5LoadProfileInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main5LoadProfileInfoResponse>,
                response: retrofit2.Response<Main5LoadProfileInfoResponse>
            ) {
                Log.e("MAIN5PROFILE: LOAD PROFILE INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val data_profile = response.body()
                    changeProfileImageTarget(baseUrl + data_profile?.profileImageUrl) // 해당 이미지 뷰에 로드
                    Log.d("이미지 url 내거 아님",data_profile?.profileImageUrl.toString())
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingRequestActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO3", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingRequestActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingRequestActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: retrofit2.Call<Main5LoadProfileInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD PROFILE INFO4", t.localizedMessage)
            }
        })
    }


    private fun changeProfileImageMine(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.relayGiverProfile)
    }

    private fun changeProfileImageTarget(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.relayTakerProfile)
    }

    //이미지 추가
    fun AddPost(postId : RequestBody, image: ArrayList<MultipartBody.Part>, userId : RequestBody) {
        val call = RetrofitBuilder.api.addPost(postId, image, userId)
        call.enqueue(object : retrofit2.Callback<Main3AddPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main3AddPostIsComplete>,
                response: retrofit2.Response<Main3AddPostIsComplete>
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

            override fun onFailure(call: retrofit2.Call<Main3AddPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    // 릴레이 업로드
    fun relayRequest(postId:RequestBody, userId:RequestBody, remainingSeconds : RequestBody) {
        val call = RetrofitBuilder.api.relayPost(postId, userId, remainingSeconds)
        call.enqueue(object : retrofit2.Callback<Main3RelayPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main3RelayPostIsComplete>,
                response: retrofit2.Response<Main3RelayPostIsComplete>
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

            override fun onFailure(call: retrofit2.Call<Main3RelayPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}

