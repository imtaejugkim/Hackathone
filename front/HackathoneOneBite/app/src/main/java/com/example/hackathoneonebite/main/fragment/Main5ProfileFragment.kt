package com.example.hackathoneonebite.main.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils.split
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.api.CreateComment
import com.example.hackathoneonebite.api.LoadAPostInfoResponse
import com.example.hackathoneonebite.api.Main5LoadPostInfoResponse
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.api.UpdateProfileRequest
import com.example.hackathoneonebite.api.UpdateProfileResponse
import com.example.hackathoneonebite.databinding.DialogMain1CommentBinding
import com.example.hackathoneonebite.databinding.DialogMain1TopBinding
import com.example.hackathoneonebite.databinding.DialogMain5Post0Binding
import com.example.hackathoneonebite.databinding.DialogMain5Post1Binding
import com.example.hackathoneonebite.databinding.DialogMain5Post2Binding
import com.example.hackathoneonebite.databinding.DialogProfileUpdateBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding
import com.example.hackathoneonebite.databinding.ItemMain5PostsBinding
import com.example.hackathoneonebite.main.MainFrameActivity
import com.example.hackathoneonebite.main.ProfileActivity
import com.google.gson.Gson
import kotlinx.coroutines.NonDisposableHandle.parent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Main5ProfileFragment : Fragment() {
    //property
    var fragment_width: Int = 0
    var fragment_height: Int = 0
    var profileBackgroundImageHeight: Int = 0
    val profileInfoViewHeight: Int = 200
    val profileInfoViewHeightWhenScollDown: Int = 180
    var transientThreshold: Int = 0 //배경이 아예 사라지는 임계 위치
    val autoDragRatioThanProfileBackgroundHeight: Float = 0.6f //프로필 배경의 얼마만큼의 비율만큼 움직여야 배경을 사라지게 할 것인가
    var bottomLayoutHeight: Int = 0
    var itemSize: Int = 0
    //data
    lateinit var binding: FragmentMain5ProfileBinding
    var id: Long = 0
    var userId: String = ""
    //network
    val baseUrl: String = "http://203.252.139.231:8080/"
    var refreshingProfile: Boolean = false
    var refreshingPost: Boolean = false
    //network handler
    private val handler = Handler(Looper.getMainLooper())
    private val profileRunnable = Runnable {
        if (isAdded) {
            Log.d("MAIN5PROFILE", "프로필 정보를 다시 로드합니다.")
            Toast.makeText(requireContext(), "프로필 정보가 로드되지 않았습니다.\n재통신을 시도합니다.", Toast.LENGTH_SHORT)
                .show()
            loadProfileInfo(id)
        }
    }
    private val postRunnable = Runnable {
        if (isAdded) {
            Log.d("MAIN5PROFILE", "포스트 정보를 다시 로드합니다.")
            Toast.makeText(
                requireContext(),
                "포스트 정보가 로드되지 않았습니다.\n서버와 재통신을 시도합니다.",
                Toast.LENGTH_SHORT
            ).show()
            loadPostInfo(id)
        }
    }

    //함수 코드 시작
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = requireActivity() as MainFrameActivity
        this.id = activity.id
        this.userId = activity.userId
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain5ProfileBinding.inflate(layoutInflater, container, false)
        initScrollview()
        loadProfileInfo(id)
        loadPostInfo(id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            fragment_width = view.width
            fragment_height = view.height
            profileBackgroundImageHeight = fragment_height - dpToPx(profileInfoViewHeight)
            transientThreshold = (profileBackgroundImageHeight * autoDragRatioThanProfileBackgroundHeight).toInt()
            init()
            initLayout()

            Log.d("fragment height", fragment_height.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        //네트워크 스케줄링 취소
        binding.swipeRefreshLayout.isRefreshing = false
        refreshingProfile = false
        refreshingPost = false
        handler.removeCallbacks(postRunnable)
        handler.removeCallbacks(profileRunnable)
    }

    private fun init() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            //네트워크 스케줄링 취소
            handler.removeCallbacks(postRunnable)
            handler.removeCallbacks(profileRunnable)
            Log.d("MAIN5PROFILE", "새로고침. 스케줄링된 로드를 취소하고 다시 로드합니다.")
            //수정 버튼 비활성화
            binding.updateProfileInfoButton.setText("")
            binding.updateProfileInfoButton.isEnabled = false
            //재로드
            refreshingProfile = false
            refreshingPost = false
            loadProfileInfo(id)
            loadPostInfo(id)
        }
        binding.updateProfileInfoButton.setOnClickListener {
            showProfileDialog()
        }
    }
    private fun loadCompleteDeleteRefreshing() {
        if(refreshingPost && refreshingProfile) {
            binding.swipeRefreshLayout.isRefreshing = false
            Log.d("MAIN5PROFILE", "새로고침 완료!")
        }
    }
    //GridLayout 높이 설정
    private fun initGridView(data_post: List<Main5LoadPostInfoResponse>) {
        val itemMargin = 2
        val columnCount = 4
        val displayMetrics = resources.displayMetrics
        itemSize = ((displayMetrics.widthPixels * 0.9f - itemMargin * columnCount * dpToPx(2)) / columnCount).toInt()
        val rowCount = data_post.size / columnCount + if (data_post.size % columnCount > 0) 1 else 0
        //대소 비교 후 bottom layout 높이 조절
        if (rowCount * (itemSize + dpToPx(2) * itemMargin) > bottomLayoutHeight) {
            val paramsProfileBottom = binding.profileBottomLayout.viewGroupProfileBottom.layoutParams
            paramsProfileBottom.height = rowCount * (itemSize + dpToPx(2) * itemMargin) + dpToPx(20)
            binding.profileBottomLayout.viewGroupProfileBottom.layoutParams = paramsProfileBottom
        }
    }

    private fun initScrollview() {
        binding.scrollView.apply {
            binding.scrollView.setOnScrollChangeListener { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                val alpha: Float =
                    if (scrollY > transientThreshold) 0f
                    else 1 - (scrollY - (1 - autoDragRatioThanProfileBackgroundHeight)) / (transientThreshold)
                binding.profileBackgroundImage.alpha = alpha
                binding.profileMainImage.alpha = alpha
            }
        }
    }

    private fun initLayout() {
        val paramsProfile = binding.profileView.layoutParams
        paramsProfile.height = fragment_height
        binding.profileView.layoutParams = paramsProfile

        val paramsProfileBottom = binding.profileBottomLayout.viewGroupProfileBottom.layoutParams
        bottomLayoutHeight = fragment_height - dpToPx(profileInfoViewHeightWhenScollDown)
        paramsProfileBottom.height = bottomLayoutHeight
        binding.profileBottomLayout.viewGroupProfileBottom.layoutParams = paramsProfileBottom
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun displayPostImage(postList: List<Main5LoadPostInfoResponse>) {
        val gridLayout = binding.profileBottomLayout.gridLayout
        gridLayout.removeAllViews()
        initGridView(postList)
        //아이템 동적 추가
        for (i in 0 until postList.size) {
            val layoutParams = FrameLayout.LayoutParams(itemSize, itemSize)
            val itemBinding = ItemMain5PostsBinding.inflate(layoutInflater)
            Glide.with(requireActivity())
                .load(baseUrl + postList[i].mainImage)
                .into(itemBinding.imageView)
            val view = itemBinding.root
            layoutParams.width = itemSize
            layoutParams.height = itemSize
            layoutParams.setMargins(dpToPx(2))
            view.layoutParams = layoutParams

            view.setOnClickListener {
                showPostDialog(postList[i].postId)
            }

            gridLayout.addView(view)
        }
    }

//<editor-fold desc="새로고침 접어놓기">
    //프로필 정보 호출
    private fun loadProfileInfo(id: Long) {
        Log.d("MAIN5PROFILE", "id : " + id.toString())
        loadProfileInfoRequest(id, id)
    }
    private fun loadProfileInfoRequest(targetId: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadProfileInfo(targetId, currentId)
        call.enqueue(object : Callback<Main5LoadProfileInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main5LoadProfileInfoResponse>,
                response: Response<Main5LoadProfileInfoResponse>
            ) {
                Log.e("MAIN5PROFILE: LOAD PROFILE INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val data_profile = response.body()
                    if (data_profile != null) {
                        if (!data_profile!!.isSuccess) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO1", "서버에서 받은 응답이 잘못됐습니다. success : false")
                            reloadProfile()
                        } else if (data_profile!!.id != id) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO2", "요청한 id와 서버에서 온 정보의 id가 다릅니다.")
                            reloadProfile()
                        } else {
                            refreshingProfile = true
                            loadCompleteDeleteRefreshing()
                            displayResponse(data_profile)
                        }
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            //Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            reloadProfile()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: LOAD PROFILE INFO3", "Failed to parse error response: $errorBody")
                            //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            reloadProfile()
                        }
                    } else {
                        //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        reloadProfile()
                    }
                }
            }
            override fun onFailure(call: Call<Main5LoadProfileInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD PROFILE INFO4", t.localizedMessage)
                reloadProfile()
            }
        })
    }
    //포트스 정보 호출
    private fun loadPostInfo(id: Long) {
        loadPostInfoRequest(id, id)
    }
    private fun loadPostInfoRequest(id: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadPostInfo(id, currentId)
        call.enqueue(object : Callback<List<Main5LoadPostInfoResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<Main5LoadPostInfoResponse>>,
                response: Response<List<Main5LoadPostInfoResponse>>
            ) {
                Log.e("MAIN5PROFILE: LOAD POST INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val postList  = response.body()
                    if (postList != null) {
                        refreshingPost = true
                        loadCompleteDeleteRefreshing()
                        displayPostImage(postList)
                    } else {
                        Log.e("MAIN5PROFILE: LOAD POST INFO1", "응답 성공. 하지만 null입니다.")
                        reloadPost()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            //Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            reloadPost()                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: LOAD POST INFO2", "Failed to parse error response: $errorBody")
                            //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            reloadPost()                        }
                    } else {
                        //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        reloadPost()
                    }
                }
            }
            override fun onFailure(call: Call<List<Main5LoadPostInfoResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE CONNECTION FAILURE: LOAD POST INFO3", t.localizedMessage)
                reloadPost()            }
        })
    }
    private fun displayResponse(response: Main5LoadProfileInfoResponse) {
        //수정 버튼 활성화
        binding.updateProfileInfoButton.setText("수정")
        binding.updateProfileInfoButton.isEnabled = true
        //fragment 정보 업데이트
        this.id = response.id
        this.userId = response.userId
        //
        changeProfileImage(baseUrl + response.profileImageUrl)
        changeBackgroundImage(baseUrl + response.backgroundImageUrl)
        binding.username.text = response.username
        binding.userId.text = response.userId
        binding.countRelay.text = response.relayReceivedCount.toString()
        binding.countFollower.text = response.followerIds.count().toString()
        binding.countFollowing.text = response.followingIds.count().toString()
    }
    private fun changeProfileImage(url: String) {
        Glide.with(requireActivity())
            .load(url)
            .into(binding.profileMainImage)
    }
    private fun changeBackgroundImage(url: String) {
        Glide.with(requireActivity())
            .load(url)
            .into(binding.profileBackgroundImage)
    }
    private fun reloadProfile() {
        handler.postDelayed(profileRunnable, 7000)
    }
    private fun reloadPost() {
        handler.postDelayed(postRunnable, 7000)
    }
//</editor-fold>

    //프로필 수정 화면
    //Gallery
    var isProfileImgChangeOrBack: Boolean = true
    var isProfileImgChanged: Boolean = false
    var isBackgroundImgChanged: Boolean = false
    var profileImgName: String = ""
    var backgroundImgName: String= ""
    var profileImageFile: File? = null
    var backgroundImageFile: File? = null
    val REQ_GALLERY = 1
    lateinit var bindingDialog: DialogProfileUpdateBinding
    val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                //서버 업로드를 위해 파일 형태로 변환
                if (isProfileImgChangeOrBack) {
                    profileImageFile = File(getRealPathFromURI(it))
                    /*val bitmap = BitmapFactory.decodeFile(profileImageFile!!.absolutePath)
                    binding.testImage.setImageBitmap(bitmap)*/
                } else {
                    backgroundImageFile = File(getRealPathFromURI(it))
                }
                //이미지 이름 가져오기
                val cursor = requireActivity().contentResolver.query(imageUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                        if (columnIndex != -1) {
                            if (isProfileImgChangeOrBack) {
                                isProfileImgChanged = true
                                profileImgName = it.getString(columnIndex)
                                bindingDialog.profileImgTextView.text = if(profileImgName == "") "재시도 필요" else profileImgName
                            } else {
                                isProfileImgChanged = false
                                backgroundImgName = it.getString(columnIndex)
                                if (backgroundImgName == "") backgroundImgName = " "
                                bindingDialog.backgroundImgTextView.text = if(backgroundImgName == "") "재시도 필요" else backgroundImgName
                            }
                        }
                    }
                }
            }
        }
    }
    //프로필 정보 변경 다이얼로그 표시
    private fun showProfileDialog() {
        //init
        isProfileImgChanged = false
        isBackgroundImgChanged = false
        profileImgName = ""
        backgroundImgName= ""
        profileImageFile = null
        backgroundImageFile = null
        //
        bindingDialog = DialogProfileUpdateBinding.inflate(layoutInflater)
        //init user info
        bindingDialog.userId.setText(this.userId)
        bindingDialog.username.setText(binding.username.text.toString())
        //
        val builder = AlertDialog.Builder(requireContext())
        val dlg = builder.setView(bindingDialog.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.BOTTOM)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //변경 버튼 리스너 등록
        bindingDialog.profileUpdateButton.setOnClickListener {
            if (isProfileImgChanged && isBackgroundImgChanged) { //API:updateProfile_userId_username_profileImg_backgroundImg
                updateProfileTextAndProfileAndBackground(bindingDialog.userId.text.toString(), bindingDialog.username.text.toString(), bindingDialog ,dlg)
            } else if (isProfileImgChanged) { //API:updateProfile_userId_username_profileImg
                updateProfileTextAndProfile(bindingDialog.userId.text.toString(), bindingDialog.username.text.toString(),bindingDialog ,dlg)
            } else if (isBackgroundImgChanged) { //API:updateProfile_userId_username_backgroundImg
                updateProfileTextAndBackground(bindingDialog.userId.text.toString(), bindingDialog.username.text.toString(),bindingDialog ,dlg)
            } else { //API:updateProfile_userId_username
                updateProfileOnlyText(bindingDialog.userId.text.toString(), bindingDialog.username.text.toString(),bindingDialog ,dlg)
            }
        }
        //프로필 userId 변경 버튼 리스너 등록
        bindingDialog.userIdButton.setOnClickListener {
            bindingDialog.userId.focusable = View.FOCUSABLE
            bindingDialog.userId.requestFocus()
            bindingDialog.profileUpdateButton.isEnabled = true
        }
        //프로필 username 변경 버튼 리스터 등록
        bindingDialog.usernameButton.setOnClickListener {
            bindingDialog.username.focusable = View.FOCUSABLE
            bindingDialog.username.requestFocus()
            bindingDialog.profileUpdateButton.isEnabled = true
        }
        //프로필 이미지 변경 버튼 리스너 등록
        bindingDialog.profileImgChangeButton.setOnClickListener {
            profileImgName = ""
            isProfileImgChangeOrBack = true
            selectGallery()
            bindingDialog.profileUpdateButton.isEnabled = true
        }
        //배경 이미지 변경 버튼 리스너 등록
        bindingDialog.backgroundImgChangeButton.setOnClickListener {
            backgroundImgName = ""
            isProfileImgChangeOrBack = false
            selectGallery()
            bindingDialog.profileUpdateButton.isEnabled = true
        }
    }
    //이미지 실제 경로 반환
    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }
    //갤러리 호출하기
    private fun selectGallery() {
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        //권한 확인
        if (readPermission == PackageManager.PERMISSION_DENIED) {
            //권한 요청
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
        } else if (readPermission == PackageManager.PERMISSION_GRANTED){
            //권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            imageResult.launch(intent)
        }
    }
    fun convertFileToMultipart(file: File): MultipartBody.Part {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", profileImgName, requestFile)
        return body
    }
    //프로필 수정 서버 통신
    //String만 수정한 경우
    private fun updateProfileOnlyText(userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg:AlertDialog) {
        updateProfileOnlyTextRequest(this.id, userId, username, dialogBinding, dlg)
    }
    private fun updateProfileOnlyTextRequest(id: Long, userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg: AlertDialog) {
        val requestBodyUserId: RequestBody = userId.toRequestBody("text/plain".toMediaType())
        val requestBodyUsername: RequestBody = username.toRequestBody("text/plain".toMediaType())
        val call = RetrofitBuilder.api.updateProfile_userId_username(id, requestBodyUserId, requestBodyUsername)
        call.enqueue(object : Callback<UpdateProfileResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                Log.e("MAIN5PROFILE: UPDATE PROFILE 00", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val updateProfileResponse = response.body()
                    if (updateProfileResponse == null) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 01", "서버에서 받은 응답이 null입니다.")
                        Toast.makeText(requireContext(), "재시도 바랍니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess && updateProfileResponse.message == "exist") {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 02", "해당 userId String이 이미 존재합니다.")
                        dialogBinding.userId.setText("")
                        dialogBinding.userId.requestFocus()
                        Toast.makeText(requireContext(), "해당 아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 03", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "유저 불일치.\n프로필 변경 불가", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("MAIN5PROFILE: UPDATE PROFILE 04", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "변경 완료", Toast.LENGTH_SHORT).show()
                        loadProfileInfo(id)
                        dlg.dismiss()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: UPDATE PROFILE 05", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE: UPDATE PROFILE 06", t.localizedMessage)
            }
        })
    }
    //String + 프로필 사진 수정한 경우
    private fun updateProfileTextAndProfile(userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg:AlertDialog) {
        updateProfileTextAndProfileRequest(this.id, userId, username, dialogBinding, dlg)
    }
    private fun updateProfileTextAndProfileRequest(id: Long, userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg: AlertDialog) {
        Log.d("main5", profileImageFile.toString())
        val requestBodyUserId: RequestBody = userId.toRequestBody("text/plain".toMediaType())
        val requestBodyUsername: RequestBody = username.toRequestBody("text/plain".toMediaType())
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestBody: RequestBody = profileImageFile!!.asRequestBody(mediaType)
        val call = RetrofitBuilder.api.updateProfile_userId_username_profileImg(id, requestBodyUserId, requestBodyUsername, requestBody)
        call.enqueue(object : Callback<UpdateProfileResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                Log.e("MAIN5PROFILE: UPDATE PROFILE 10", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val updateProfileResponse = response.body()
                    if (updateProfileResponse == null) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 11", "서버에서 받은 응답이 null입니다.")
                        Toast.makeText(requireContext(), "재시도 바랍니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess && updateProfileResponse.message == "exist") {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 12", "해당 userId String이 이미 존재합니다.")
                        dialogBinding.userId.setText("")
                        dialogBinding.userId.requestFocus()
                        Toast.makeText(requireContext(), "해당 아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 13", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "유저 불일치.\n프로필 변경 불가", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("MAIN5PROFILE: UPDATE PROFILE 14", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "변경 완료", Toast.LENGTH_SHORT).show()
                        loadProfileInfo(id)
                        dlg.dismiss()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: UPDATE PROFILE 15", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE: UPDATE PROFILE 16", t.localizedMessage)
            }
        })
    }
    //String + 배경 사진 수정한 경우
    private fun updateProfileTextAndBackground(userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg:AlertDialog) {
        updateProfileTextAndBackgroundRequest(this.id, userId, username, dialogBinding, dlg)
    }
    private fun updateProfileTextAndBackgroundRequest(id: Long, userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg: AlertDialog) {
        val requestBodyUserId: RequestBody = userId.toRequestBody("text/plain".toMediaType())
        val requestBodyUsername: RequestBody = username.toRequestBody("text/plain".toMediaType())
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestBody: RequestBody = backgroundImageFile!!.asRequestBody(mediaType)
        val call = RetrofitBuilder.api.updateProfile_userId_username_backgroundImg(id, requestBodyUserId, requestBodyUsername, requestBody)
        call.enqueue(object : Callback<UpdateProfileResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                Log.e("MAIN5PROFILE: UPDATE PROFILE 10", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val updateProfileResponse = response.body()
                    if (updateProfileResponse == null) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 11", "서버에서 받은 응답이 null입니다.")
                        Toast.makeText(requireContext(), "재시도 바랍니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess && updateProfileResponse.message == "exist") {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 12", "해당 userId String이 이미 존재합니다.")
                        dialogBinding.userId.setText("")
                        dialogBinding.userId.requestFocus()
                        Toast.makeText(requireContext(), "해당 아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 13", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "유저 불일치.\n프로필 변경 불가", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("MAIN5PROFILE: UPDATE PROFILE 14", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "변경 완료", Toast.LENGTH_SHORT).show()
                        loadProfileInfo(id)
                        dlg.dismiss()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: UPDATE PROFILE 15", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE: UPDATE PROFILE 16", t.localizedMessage)
            }
        })
    }
    //String + 프로필 사진 + 배경 사진 수정한 경우
    private fun updateProfileTextAndProfileAndBackground(userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg:AlertDialog) {
        updateProfileTextAndProfileAndBackgroundRequest(this.id, userId, username, dialogBinding, dlg)
    }
    private fun updateProfileTextAndProfileAndBackgroundRequest(id: Long, userId: String, username: String, dialogBinding: DialogProfileUpdateBinding, dlg: AlertDialog) {
        val requestBodyUserId: RequestBody = userId.toRequestBody("text/plain".toMediaType())
        val requestBodyUsername: RequestBody = username.toRequestBody("text/plain".toMediaType())
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestBodyProfile: RequestBody = profileImageFile!!.asRequestBody(mediaType)
        val requestBodyBackground: RequestBody = backgroundImageFile!!.asRequestBody(mediaType)
        val call = RetrofitBuilder.api.updateProfile_userId_username_profileImg_backgroundImg(id, requestBodyUserId, requestBodyUsername, requestBodyProfile, requestBodyBackground)
        call.enqueue(object : Callback<UpdateProfileResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                Log.e("MAIN5PROFILE: UPDATE PROFILE 10", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val updateProfileResponse = response.body()
                    if (updateProfileResponse == null) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 11", "서버에서 받은 응답이 null입니다.")
                        Toast.makeText(requireContext(), "재시도 바랍니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess && updateProfileResponse.message == "exist") {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 12", "해당 userId String이 이미 존재합니다.")
                        dialogBinding.userId.setText("")
                        dialogBinding.userId.requestFocus()
                        Toast.makeText(requireContext(), "해당 아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                    } else if (!updateProfileResponse.isSuccess) {
                        Log.e("MAIN5PROFILE: UPDATE PROFILE 13", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "유저 불일치.\n프로필 변경 불가", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("MAIN5PROFILE: UPDATE PROFILE 14", updateProfileResponse.message)
                        Toast.makeText(requireContext(), "변경 완료", Toast.LENGTH_SHORT).show()
                        loadProfileInfo(id)
                        dlg.dismiss()
                    }
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN5PROFILE: UPDATE PROFILE 15", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN5PROFILE: UPDATE PROFILE 16", t.localizedMessage)
            }
        })
    }

    //<editor-fold desc="댓글 부분 접어놓기">
    lateinit var bindingCommentDialog: DialogMain1CommentBinding
    lateinit var adapter_comment: AdapterMain1HomeComment
    private fun showPostDialog(postId: Long) {
        //포스트 정보 표시
        loadAPostInfo(postId)
    }

    //댓글 Dialog
    private fun showCommentDialog(aPostInfo: LoadAPostInfoResponse) {
        //dialog 객체 생성
        bindingCommentDialog = DialogMain1CommentBinding.inflate(layoutInflater)
        initCommentRecyclerView()
        val builder = AlertDialog.Builder(requireContext())
        val dlg = builder.setView(bindingCommentDialog.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.BOTTOM)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog 사라질 때 콜백 함수
        dlg.setOnDismissListener {
            bindingCommentDialog.apply {
                textTextView.text = ""
                userIdStringTextView1.text = ""
                userIdStringTextView2.text = ""
                userIdStringTextView3.text = ""
                userIdStringTextView4.text = ""
                dateTextView.text = ""
            }
        }
        //댓글 정보 불러오기
        loadComments(aPostInfo.id)
        //댓글 창 위쪽 정보 초기화
        if (aPostInfo.participantUserIdStrings.count() == 1) {
            bindingCommentDialog.userIdStringTextView1.text = aPostInfo.participantUserIdStrings[0]
        } else if (aPostInfo.participantUserIdStrings.count() == 4){
            bindingCommentDialog.userIdStringTextView1.text = aPostInfo.participantUserIdStrings[0]
            bindingCommentDialog.userIdStringTextView2.text = aPostInfo.participantUserIdStrings[1]
            bindingCommentDialog.userIdStringTextView3.text = aPostInfo.participantUserIdStrings[2]
            bindingCommentDialog.userIdStringTextView4.text = aPostInfo.participantUserIdStrings[3]
        }
        bindingCommentDialog.textTextView.text = aPostInfo.text
        bindingCommentDialog.dateTextView.text = aPostInfo.date
        bindingCommentDialog.addCommentButton.setOnClickListener {
            createComment(aPostInfo.id,this.id,bindingCommentDialog.addCommentEditTextView.text.toString())
        }
    }
    private fun initCommentRecyclerView() {
        bindingCommentDialog.apply {
            //리사이클러뷰 생성
            commentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter_comment = AdapterMain1HomeComment(requireContext())
            adapter_comment.userIdClickListener = object : AdapterMain1HomeComment.OnItemClickListener {
                override fun OnItemClick(id: Long) {
                    startProfileActivity(id)
                }
            }
            commentRecyclerView.adapter = adapter_comment
        }
    }
    //다른 프로필 Activity 실행
    private fun startProfileActivity(targetId: Long) {
        val i = Intent(requireContext(), ProfileActivity::class.java)
        i.putExtra("requesterId",this.id)
        i.putExtra("targetId",targetId)
        startActivity(i)
    }
    //네에에에트으으으으으워어어어어크으으으으으으
    //타임 차이 계산
    private fun calculateTimeDifference(time: String): String {
        var beforeTime = time
        if (beforeTime.length < 26) {
            for (i in 0..25 - time.length) {
                beforeTime += "0"
            }
            val parts = beforeTime.split(".")
            val nanoSeconds = ((parts[1]).toInt() - 1).toString()
            beforeTime = parts[0] + "." + nanoSeconds
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val timeObject = LocalDateTime.parse(beforeTime, formatter)

        val duration = Duration.between(timeObject, LocalDateTime.now())

        val days = duration.toDays()
        val hours = duration.toHours() - days * 24
        val minutes = duration.toMinutes() - days * 24 * 60 - hours * 60

        return when {
            days > 0 -> "$days 일 전"
            hours > 0 -> "$hours 시간 전"
            minutes > 0 -> "$minutes 분 전"
            else -> "방금 전"
        }
    }
    //포스트 정보 로드
    private fun loadAPostInfo(postId: Long) {
        loadAPostInfoRequest(postId)
    }
    private fun loadAPostInfoRequest(postId: Long) {
        val call = RetrofitBuilder.api.loadAPostInfoRequest(postId)
        call.enqueue(object : Callback<LoadAPostInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<LoadAPostInfoResponse>,
                response: Response<LoadAPostInfoResponse>
            ) {
                Log.e("MAIN1HOME_COMMENT21", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val aPostInfo  = response.body()
                    if (aPostInfo == null) {
                        Log.e("MAIN1HOME_COMMENT22", "응답이 null입니다.")
                        return
                    } else {
                        when (aPostInfo.theme) {
                            0 -> {
                                val bindingAPost = DialogMain5Post0Binding.inflate(layoutInflater)
                                val builder = AlertDialog.Builder(requireContext())
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1frame1)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2frame1)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3frame1)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4frame1)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = calculateTimeDifference(aPostInfo.date)
                                //좋아요 반영
                                if (aPostInfo.likeClicked) {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                                } else {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                                }
                                //글 반영
                                bindingAPost.messageTextView.text = aPostInfo.text
                                //프사 반영
                                if (aPostInfo.participantsUserProfileUrl.count() == 1) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[3])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.VISIBLE
                                }
                                //클릭 리스너
                                bindingAPost.messageTextView.setOnClickListener {
                                    showCommentDialog(aPostInfo)
                                }
                            }
                            1 -> {
                                val bindingAPost = DialogMain5Post1Binding.inflate(layoutInflater)
                                val builder = AlertDialog.Builder(requireContext())
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1frame2)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2frame2)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3frame2)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4frame2)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = calculateTimeDifference(aPostInfo.date)
                                //좋아요 반영
                                if (aPostInfo.likeClicked) {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                                } else {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                                }
                                //글 반영
                                bindingAPost.messageTextView.text = aPostInfo.text
                                //프사 반영
                                if (aPostInfo.participantsUserProfileUrl.count() == 1) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[3])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.VISIBLE
                                }
                                //클릭 리스너
                                bindingAPost.messageTextView.setOnClickListener {
                                    showCommentDialog(aPostInfo)
                                }
                            }
                            2 -> {
                                val bindingAPost = DialogMain5Post2Binding.inflate(layoutInflater)
                                val builder = AlertDialog.Builder(requireContext())
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3)
                                Glide.with(requireContext())
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = calculateTimeDifference(aPostInfo.date)
                                //좋아요 반영
                                if (aPostInfo.likeClicked) {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                                } else {
                                    bindingAPost.likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                                }
                                //글 반영
                                bindingAPost.messageTextView.text = aPostInfo.text
                                //프사 반영
                                if (aPostInfo.participantsUserProfileUrl.count() == 1) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(requireContext())
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[3])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.VISIBLE
                                }
                                //클릭 리스너
                                bindingAPost.messageTextView.setOnClickListener {
                                    showCommentDialog(aPostInfo)
                                }
                            }
                        }
                    }
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_COMMENT25", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LoadAPostInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_COMMENT26: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //댓글 로드
    private fun loadComments(postId: Long) {
        loadCommentsRequest(postId)
    }
    private fun loadCommentsRequest(postId: Long) {
        val call = RetrofitBuilder.api.loadComments(postId)
        call.enqueue(object : Callback<List<CommentResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<CommentResponse>>,
                response: Response<List<CommentResponse>>
            ) {
                Log.e("MAIN1HOME_COMMENT31", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val commentsList  = response.body()
                    if (commentsList == null) {
                        Log.e("MAIN1HOME_COMMENT32", "응답이 null입니다.")
                        return
                    } else if (commentsList.count() == 0) {
                        Log.e("MAIN1HOME_COMMENT33", "댓글이 없습니다.")
                        return
                    } else {
                        Log.e("MAIN1HOME_COMMENT34", "댓글 로드 성공!")
                        adapter_comment.data = commentsList.toMutableList()
                        adapter_comment.notifyDataSetChanged()
                    }
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_COMMENT35", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_COMMENT36: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //댓글 추가
    private fun createComment(postId: Long, userId: Long, commentContent: String) {
        val request = CreateComment(postId, userId, commentContent)
        createCommentRequest(request, postId)
    }
    //키보드 숨기기
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun createCommentRequest(request: CreateComment, postId: Long) {
        val call = RetrofitBuilder.api.createComment(request)
        call.enqueue(object : Callback<CommentResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                Log.e("MAIN1HOME_CREATE_COMMENT41", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val commentResponse  = response.body()
                    if (commentResponse == null) {
                        Log.e("MAIN1HOME_CREATE_COMMENT42", "응답이 null입니다.")
                        return
                    } else  {
                        bindingCommentDialog.addCommentEditTextView.setText("")
                        hideKeyboard(requireActivity())
                        loadComments(postId)
                    }
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_CREATE_COMMENT43", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_CREATE_COMMENT44: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //</editor-fold>
}