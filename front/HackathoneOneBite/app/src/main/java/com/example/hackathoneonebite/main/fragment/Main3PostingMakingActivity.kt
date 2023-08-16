package com.example.hackathoneonebite.main.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.MyApplication
import com.example.hackathoneonebite.MyApplication.Companion.imageByteArrays
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3RelayPostResponse
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.main.MainFrameActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.time.LocalDateTime
import retrofit2.Callback
import retrofit2.Response


class Main3PostingMakingActivity : AppCompatActivity() {

    private lateinit var imagesFill: Array<String> // 사진 채워있는 유무 true/false
    private lateinit var images: Array<ByteArray> // image 변환 전 byteArray들
    private lateinit var imgArray : Array<String> // 사진 직접 담는 array
    private lateinit var imgPartArray : Array<Int> // 사진 채워져 있는 유무 0/1
    private var participantUserIds: ArrayList<Long> = arrayListOf(0)
    private var imagePartSize = 0
    private var theme = 0
    var id : Long = 0
    var userId: String = ""
    var postId : Long = 0
    var requestNumber : Int = -1
    val baseUrl: String = "http://203.252.139.231:8080/"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_making)

        requestNumber = intent.getIntExtra("requestNumber", requestNumber)

        imagesFill = Array(4) { "" }
        images = Array(4) { ByteArray(0) }
        imgArray = Array(4) { "" }

        //초기 생성 게시물 request
        if (requestNumber == 0) {
            // Intent에서 클릭된 레이아웃의 ID를 가져옴
            id = intent.getLongExtra("id", 0)
            theme = intent.getIntExtra("layout_id", 0)
            userId = intent.getStringExtra("userId") + ""
            Log.d("id", id.toString())
            Log.d("userId", userId)
        } else {
            postId = intent.getLongExtra("postId", 0)
            LoadPost(postId)

        }

        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        postImageLayoutFilm.visibility = View.INVISIBLE
        postImageLayout1.visibility = View.INVISIBLE
        postImageLayout2.visibility = View.INVISIBLE

        val relayButton1 = findViewById<Button>(R.id.relayButton1)
        val uploadButton1 = findViewById<Button>(R.id.uploadButton1)
        val relayButton2 = findViewById<Button>(R.id.relayButton2)
        val uploadButton2 = findViewById<Button>(R.id.uploadButton2)
        val disabledButton1 = findViewById<Button>(R.id.unActiveButton1)
        val disabledButton2 = findViewById<Button>(R.id.unActiveButton2)

        when (theme) {
            0 -> {
                postImageLayoutFilm.visibility = View.VISIBLE
                disabledButton1.visibility = View.VISIBLE
                contentFrameFilm()
            }

            1 -> {
                postImageLayout1.visibility = View.VISIBLE
                disabledButton1.visibility = View.VISIBLE
                contentFrame1()
            }

            2 -> {
                postImageLayout2.visibility = View.VISIBLE
                disabledButton2.visibility = View.VISIBLE
                contentFrame2()
            }
        }

        val leftArrow = findViewById<ImageView>(R.id.leftArrow)
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        updateButtonsVisibility(theme)

        relayButton1.setOnClickListener {
            val message = ""
            val post = Post(imagesFill, theme, userId, 0, LocalDateTime.now(), message, false)
            imgPartArray = Array(4) { 0 }

            val imageParts = arrayOfNulls<MultipartBody.Part>(4)

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart =
                        MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    imgPartArray[i]++
                }
            }

            val intent =
                Intent(this@Main3PostingMakingActivity, Main3PostingRelaySearchActivity::class.java)
            intent.putExtra("post_data", post)
            intent.putExtra("imagePartSize", imageParts.size)
            intent.putExtra("id", id)
            intent.putExtra("userId", userId)
            intent.putExtra("requestNumber", requestNumber)

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart =
                        MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    val buffer = okio.Buffer()
                    requestFile.writeTo(buffer)
                    val byteArray = buffer.readByteArray()
                    imageByteArrays.add(byteArray)
                }
            }

            startActivity(intent)
        }

        relayButton2.setOnClickListener {
            val message = "백엔드야 메세지 받아라"
            val post = Post(imagesFill, theme, userId, 0, LocalDateTime.now(), message, false)
            imgPartArray = Array(4) { 0 }

            val imageParts = arrayOfNulls<MultipartBody.Part>(4)

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart =
                        MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    imgPartArray[i]++
                }
            }

            val intent =
                Intent(this@Main3PostingMakingActivity, Main3PostingRelaySearchActivity::class.java)
            intent.putExtra("post_data", post)
            intent.putExtra("imagePartSize", imageParts.size)
            intent.putExtra("id", id)
            intent.putExtra("userId", userId)
            intent.putExtra("requestNumber", requestNumber)

            //val imageByteArrays = ArrayList<ByteArray>() // Image data extraction list

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart =
                        MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    val buffer = okio.Buffer()
                    requestFile.writeTo(buffer)
                    val byteArray = buffer.readByteArray()
                    imageByteArrays.add(byteArray)
                }
            }

            /*for (i in 0 until imageByteArrays.size) {
                intent.putExtra("imageByteArrays$i", imageByteArrays[i]) // Pass individual byte arrays to the next screen
                Log.d("new 보냄 ", imageByteArrays[i].toString())
            }*/

            startActivity(intent)
        }
        uploadButton1.setOnClickListener {
            //게시물 4개 업로드시
            if (requestNumber == 0) {
                val message = "백엔드야 메세지 받아라"
                val post = Post(imagesFill, theme, userId, 0, LocalDateTime.now(), message, false)
                imgPartArray = Array(4) { 0 }

                val imageParts = arrayOfNulls<MultipartBody.Part>(4)

                for (i in 0 until 4) {
                    val image = images[i]
                    if (image.isNotEmpty()) {
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                        val partName = "image${i + 1}.jpg"
                        val imagePart =
                            MultipartBody.Part.createFormData("image", partName, requestFile)
                        imageParts[i] = imagePart
                        imgPartArray[i]++
                    }
                }

                val intent = Intent(
                    this@Main3PostingMakingActivity,
                    Main3PostingNowUploadActivity::class.java
                )
                intent.putExtra("post_data", post)
                intent.putExtra("imagePartSize", imageParts.size)
                intent.putExtra("id", id)
                intent.putExtra("userId", userId)

                for (i in 0 until 4) {
                    val image = images[i]
                    if (image.isNotEmpty()) {
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                        val partName = "image${i + 1}.jpg"
                        val imagePart =
                            MultipartBody.Part.createFormData("image", partName, requestFile)
                        imageParts[i] = imagePart
                        val buffer = okio.Buffer()
                        requestFile.writeTo(buffer)
                        val byteArray = buffer.readByteArray()
                        imageByteArrays.add(byteArray)
                    }
                }

                startActivity(intent)
            }
            // relay 마지막 받은 사람
            else {
                var imageByteArrayIndex = 0 // imageByteArrays 리스트의 인덱스
                val imageParts = ArrayList<MultipartBody.Part>()
                images = Array(4) { ByteArray(0) }

                for (i in 0..3) {
                    Log.d("imageArray$i", imgArray[i])
                    if (imgArray[i] == "true") {
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
                    } else {
                        val emptyRequestBody =
                            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
                        val emptyPart = MultipartBody.Part.createFormData(
                            "empty_part_name",
                            "",
                            emptyRequestBody
                        )
                        imageParts.add(emptyPart)
                    }

                    //addImage 해야함

                }
                val intent = Intent(this@Main3PostingMakingActivity, MainFrameActivity::class.java)
                Toast.makeText(this, "모든 사진 게시 완료!", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }

            uploadButton2.setOnClickListener {
                //게시물 4개 업로드시
                if (requestNumber == 0) {
                    val message = "백엔드야 메세지 받아라"
                    val post =
                        Post(imagesFill, theme, userId, 0, LocalDateTime.now(), message, false)
                    imgPartArray = Array(4) { 0 }

                    val imageParts = arrayOfNulls<MultipartBody.Part>(4)

                    for (i in 0 until 4) {
                        val image = images[i]
                        if (image.isNotEmpty()) {
                            val requestFile =
                                RequestBody.create("image/*".toMediaTypeOrNull(), image)
                            val partName = "image${i + 1}.jpg"
                            val imagePart =
                                MultipartBody.Part.createFormData("image", partName, requestFile)
                            imageParts[i] = imagePart
                            imgPartArray[i]++
                        }
                    }

                    val intent = Intent(
                        this@Main3PostingMakingActivity,
                        Main3PostingNowUploadActivity::class.java
                    )
                    intent.putExtra("post_data", post)
                    intent.putExtra("imagePartSize", imageParts.size)
                    intent.putExtra("id", id)
                    intent.putExtra("userId", userId)

                    for (i in 0 until 4) {
                        val image = images[i]
                        if (image.isNotEmpty()) {
                            val requestFile =
                                RequestBody.create("image/*".toMediaTypeOrNull(), image)
                            val partName = "image${i + 1}.jpg"
                            val imagePart =
                                MultipartBody.Part.createFormData("image", partName, requestFile)
                            imageParts[i] = imagePart
                            val buffer = okio.Buffer()
                            requestFile.writeTo(buffer)
                            val byteArray = buffer.readByteArray()
                            imageByteArrays.add(byteArray)
                        }
                    }

                    startActivity(intent)
                }
                // relay 마지막 받은 사람
                else {
                    var imageByteArrayIndex = 0 // imageByteArrays 리스트의 인덱스
                    val imageParts = ArrayList<MultipartBody.Part>()
                    images = Array(4) { ByteArray(0) }

                    for (i in 0..3) {
                        Log.d("imageArray$i", imgArray[i])
                        if (imgArray[i] == "true") {
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
                        } else {
                            val emptyRequestBody =
                                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
                            val emptyPart = MultipartBody.Part.createFormData(
                                "empty_part_name",
                                "",
                                emptyRequestBody
                            )
                            imageParts.add(emptyPart)
                        }

                        //addImage 해야함

                    }
                    val intent =
                        Intent(this@Main3PostingMakingActivity, MainFrameActivity::class.java)
                    Toast.makeText(this, "모든 사진 게시 완료!", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            }
        }
    }


    fun LoadPost(postId : Long){
        val call = RetrofitBuilder.api.main3LoadRelayPostRequest(postId)
        call.enqueue(object : Callback<Main3RelayPostResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main3RelayPostResponse>,
                response: Response<Main3RelayPostResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    for(i in 0..3){
                        imgArray[i] = userResponse?.images?.get(i).toString()
                        if(userResponse?.images?.get(i) !=null){
                            val selectedImageView = findImageViewForCurrentContentFrame(i, userResponse.theme)
                            Glide.with(this@Main3PostingMakingActivity)
                                .load(baseUrl + userResponse.images[i])
                                .into(selectedImageView)
                            imagesFill[i] = true.toString()
                        }
                    }
                    theme = userResponse!!.theme
                    id = userResponse.id

                    Log.d("RESPONSE: ", "Success")
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@Main3PostingMakingActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@Main3PostingMakingActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Main3PostingMakingActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Main3RelayPostResponse>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }

    private fun updateButtonsVisibility(theme : Int) {
        val relayButton1 = findViewById<Button>(R.id.relayButton1)
        val uploadButton1 = findViewById<Button>(R.id.uploadButton1)
        val disabledButton1 = findViewById<Button>(R.id.unActiveButton1)
        val relayButton2 = findViewById<Button>(R.id.relayButton2)
        val uploadButton2 = findViewById<Button>(R.id.uploadButton2)
        val disabledButton2 = findViewById<Button>(R.id.unActiveButton2)

        when (theme) {
            in 0..1 -> {
                when (imagesFill.count { it.isNotBlank() }) {
                    0 -> {
                        relayButton1.visibility = View.GONE
                        uploadButton1.visibility = View.GONE
                        disabledButton1.visibility = View.VISIBLE
                    }
                    in 1..3 -> {
                        relayButton1.visibility = View.VISIBLE
                        uploadButton1.visibility = View.GONE
                        disabledButton1.visibility = View.GONE
                    }
                    4 -> {
                        relayButton1.visibility = View.GONE
                        uploadButton1.visibility = View.VISIBLE
                        disabledButton1.visibility = View.GONE
                    }
                }
            }
            2 -> {
                when (imagesFill.count { it.isNotBlank() }) {
                    0 -> {
                        relayButton2.visibility = View.GONE
                        uploadButton2.visibility = View.GONE
                        disabledButton2.visibility = View.VISIBLE
                    }
                    in 1..3 -> {
                        relayButton2.visibility = View.VISIBLE
                        uploadButton2.visibility = View.GONE
                        disabledButton2.visibility = View.GONE
                    }
                    4 -> {
                        relayButton2.visibility = View.GONE
                        uploadButton2.visibility = View.VISIBLE
                        disabledButton2.visibility = View.GONE
                    }
                }
            }
        }
    }



    private fun contentFrameFilm() {
        val img1 = findViewById<ImageView>(R.id.imageView1)
        val img2 = findViewById<ImageView>(R.id.imageView2)
        val img3 = findViewById<ImageView>(R.id.imageView3)
        val img4 = findViewById<ImageView>(R.id.imageView4)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                intent.putExtra("layout_id", theme)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                imagesFill[index] = true.toString()

            }
        }
    }

    private fun contentFrame1() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame1)
        val img2 = findViewById<ImageView>(R.id.imageView2frame1)
        val img3 = findViewById<ImageView>(R.id.imageView3frame1)
        val img4 = findViewById<ImageView>(R.id.imageView4frame1)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                intent.putExtra("layout_id", theme)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                imagesFill[index] = true.toString()
            }
        }
    }

    private fun contentFrame2() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame2)
        val img2 = findViewById<ImageView>(R.id.imageView2frame2)
        val img3 = findViewById<ImageView>(R.id.imageView3frame2)
        val img4 = findViewById<ImageView>(R.id.imageView4frame2)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                intent.putExtra("layout_id", theme)
                Log.d("layout_id",theme.toString())
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                imagesFill[index] = true.toString()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_SELECT_IMAGE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageByteArray = data?.getByteArrayExtra("selected_image")
            val contentsId = data?.getIntExtra("contents_id", 2) ?: 2

            if (selectedImageByteArray != null) {
                val selectedImageView = findImageViewForCurrentContentFrame(contentsId, theme)
                val selectedBitmap = BitmapFactory.decodeByteArray(selectedImageByteArray, 0, selectedImageByteArray.size)
                selectedImageView.setImageBitmap(selectedBitmap)

                imagesFill[contentsId] = true.toString()
                images[contentsId] = selectedImageByteArray
                imagePartSize++
                Log.d("다시 받은 layoutid",theme.toString())
                updateButtonsVisibility(theme)

            }
        }
    }

    private fun findImageViewForCurrentContentFrame(contentsId: Int, layoutId: Int): ImageView {

        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        when (layoutId) {
            0 -> {
                // LayoutFilm의 경우
                return when (contentsId) {
                    0 -> postImageLayoutFilm.findViewById(R.id.imageView1)
                    1 -> postImageLayoutFilm.findViewById(R.id.imageView2)
                    2 -> postImageLayoutFilm.findViewById(R.id.imageView3)
                    3 -> postImageLayoutFilm.findViewById(R.id.imageView4)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            1 -> {
                // Layout1의 경우
                return when (contentsId) {
                    0 -> postImageLayout1.findViewById(R.id.imageView1frame1)
                    1 -> postImageLayout1.findViewById(R.id.imageView2frame1)
                    2 -> postImageLayout1.findViewById(R.id.imageView3frame1)
                    3 -> postImageLayout1.findViewById(R.id.imageView4frame1)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            2 -> {
                // Layout2의 경우
                return when (contentsId) {
                    0 -> postImageLayout2.findViewById(R.id.imageView1frame2)
                    1 -> postImageLayout2.findViewById(R.id.imageView2frame2)
                    2 -> postImageLayout2.findViewById(R.id.imageView3frame2)
                    3 -> postImageLayout2.findViewById(R.id.imageView4frame2)
                    else -> throw IllegalArgumentException("Invalid contentsId")
                }
            }
            else -> throw IllegalArgumentException("Invalid layoutId")


        }
    }

    //릴레이 마지막 업로드
    fun Upload(image: ArrayList<MultipartBody.Part>, theme: RequestBody, userId: RequestBody, musicNum : RequestBody, message: RequestBody) {
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId, musicNum, message)
        call.enqueue(object : retrofit2.Callback<Main3UploadPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: retrofit2.Call<Main3UploadPostIsComplete>,
                response: retrofit2.Response<Main3UploadPostIsComplete>
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
                                this@Main3PostingMakingActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(
                                this@Main3PostingMakingActivity,
                                "오류가 발생했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Main3PostingMakingActivity,
                            "오류가 발생했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Main3UploadPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }
}