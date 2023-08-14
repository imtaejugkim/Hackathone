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
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main3UploadPostIsComplete
import com.example.hackathoneonebite.api.RetrofitBuilder
import kotlinx.coroutines.NonCancellable
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
    private lateinit var imgArray : Array<String>
    private lateinit var imgPartArray : Array<Int>
    private var participantUserIds: ArrayList<Long> = arrayListOf(0)
    private var imagePartSize = 0
    private var theme = 0
    var id : Long = 0
    var userId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_making)

        imagesFill = Array(4) { "" }
        images = Array(4) { ByteArray(0) }
        imgArray = Array(4){ "" }

        // Intent에서 클릭된 레이아웃의 ID를 가져옴
        id = intent.getLongExtra("id", 0)
        theme = intent.getIntExtra("layout_id", 0)
        userId = intent.getStringExtra("userId") + ""
        Log.d("id",id.toString())
        Log.d("userId",userId)


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
            val message = "백엔드야 메세지 받아라"
            val post = Post(imagesFill, theme, userId, 0, LocalDateTime.now(), message, false)
            imgPartArray = Array(4) { 0 }

            val imageParts = arrayOfNulls<MultipartBody.Part>(4)

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart = MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    imgPartArray[i]++
                }
            }

            val intent = Intent(this@Main3PostingMakingActivity, Main3PostingRelaySearchActivity::class.java)
            intent.putExtra("post_data", post)
            intent.putExtra("imagePartSize", imageParts.size)

            val imageByteArrays = ArrayList<ByteArray>() // Image data extraction list

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart = MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart // Store the MultipartBody.Part in the array
                    val buffer = okio.Buffer() // Create an Okio Buffer
                    requestFile.writeTo(buffer) // Write the RequestBody to the buffer
                    val byteArray = buffer.readByteArray() // Convert buffer to byte array
                    imageByteArrays.add(byteArray) // Store the byte array separately
                }
            }

            for (i in 0 until imageByteArrays.size) {
                intent.putExtra("imageByteArrays$i", imageByteArrays[i]) // Pass individual byte arrays to the next screen
                Log.d("new 보냄 ", imageByteArrays[i].toString())
            }

            startActivity(intent)
        }

        relayButton2.setOnClickListener {
            val message = "백엔드야 메세지 받아라"
            val post = Post(imagesFill, theme, userId, 0,LocalDateTime.now(), message,false)



            val intent = Intent(this@Main3PostingMakingActivity, Main3PostingRelaySearchActivity::class.java)
            intent.putExtra("post_data", post)
            startActivity(intent)

        }
        uploadButton1.setOnClickListener {
            val requestFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), images[0])
            val requestFile2 = RequestBody.create("image/*".toMediaTypeOrNull(), images[1])
            val requestFile3 = RequestBody.create("image/*".toMediaTypeOrNull(), images[2])
            val requestFile4 = RequestBody.create("image/*".toMediaTypeOrNull(), images[3])

            val imagePart = MultipartBody.Part.createFormData("image", "image1.jpg", requestFile1)
            val imagePart2 = MultipartBody.Part.createFormData("image", "image2.jpg", requestFile2)
            val imagePart3 = MultipartBody.Part.createFormData("image", "image3.jpg", requestFile3)
            val imagePart4 = MultipartBody.Part.createFormData("image", "image4.jpg", requestFile4)
            val imageParts = ArrayList<MultipartBody.Part>()

            //val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), theme.toString())
            //val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            //val message = RequestBody.create("text/plain".toMediaTypeOrNull(), "hello")

            //imageParts.add(imagePart)
            //imageParts.add(imagePart2)
            //imageParts.add(imagePart3)
            //imageParts.add(imagePart4)

            //Upload(imageParts, theme, userId, message)
            val post = Post(imagesFill, theme, userId, 0,LocalDateTime.now(), "",false)
            val intent = Intent(this@Main3PostingMakingActivity, Main3PostingNowUploadActivity::class.java)
            intent.putExtra("post_data", post)
            intent.putExtra("imagePartSize", imagePartSize)
            for (i in 0 until imageParts.size) {
                val part = imageParts[i]
                intent.putExtra("imagePart$i", part.headers?.get("Content-Disposition"))
                Log.d("파트",part.toString())
            }
            startActivity(intent)
        }
        uploadButton2.setOnClickListener {
            val requestFile1 = RequestBody.create("image/*".toMediaTypeOrNull(), images[0])
            val requestFile2 = RequestBody.create("image/*".toMediaTypeOrNull(), images[1])
            val requestFile3 = RequestBody.create("image/*".toMediaTypeOrNull(), images[2])
            val requestFile4 = RequestBody.create("image/*".toMediaTypeOrNull(), images[3])

            val imagePart = MultipartBody.Part.createFormData("image", "image1.jpg", requestFile1)
            val imagePart2 = MultipartBody.Part.createFormData("image", "image2.jpg", requestFile2)
            val imagePart3 = MultipartBody.Part.createFormData("image", "image3.jpg", requestFile3)
            val imagePart4 = MultipartBody.Part.createFormData("image", "image4.jpg", requestFile4)
            val imageParts = ArrayList<MultipartBody.Part>()

            //val themePart = RequestBody.create("text/plain".toMediaTypeOrNull(), theme.toString())
            //val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            //val message = RequestBody.create("text/plain".toMediaTypeOrNull(), "hello")

            imageParts.add(imagePart)
            imageParts.add(imagePart2)
            imageParts.add(imagePart3)
            imageParts.add(imagePart4)

            val message = "백엔드야 메세지 받아라"
            //Upload(imageParts, theme, userId, message)
            val post = Post(imagesFill, theme, userId, 0,LocalDateTime.now(), message,false)
            val intent = Intent(this@Main3PostingMakingActivity, Main3PostingNowUploadActivity::class.java)
            intent.putExtra("post_data", post)
            val imageByteArrays = ArrayList<ByteArray>() // Image data extraction list

            for (i in 0 until 4) {
                val image = images[i]
                if (image.isNotEmpty()) {
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), image)
                    val partName = "image${i + 1}.jpg"
                    val imagePart = MultipartBody.Part.createFormData("image", partName, requestFile)
                    imageParts[i] = imagePart
                    val buffer = okio.Buffer()
                    requestFile.writeTo(buffer)
                    val byteArray = buffer.readByteArray()
                    imageByteArrays.add(byteArray)
                }
            }

            for (i in 0 until imageByteArrays.size) {
                intent.putExtra("imageByteArrays$i", imageByteArrays[i])
                Log.d("new 보냄 ", imageByteArrays[i].toString())
            }
            startActivity(intent)
        }
    }

    /*fun Upload(image: ArrayList<MultipartBody.Part>, theme: Int, userId: String, message : String){
        val call = RetrofitBuilder.api.uploadPost(image, theme, userId, 0 , message!!)
        call.enqueue(object : Callback<Main3UploadPostIsComplete> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main3UploadPostIsComplete>,
                response: Response<Main3UploadPostIsComplete>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
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

            override fun onFailure(call: Call<Main3UploadPostIsComplete>, t: Throwable) {
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
            }
        })
    }*/

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
}