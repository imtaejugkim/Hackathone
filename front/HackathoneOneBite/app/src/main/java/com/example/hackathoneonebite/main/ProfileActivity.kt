package com.example.hackathoneonebite.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.api.CreateComment
import com.example.hackathoneonebite.api.FollowToggleResponse
import com.example.hackathoneonebite.api.LoadAPostInfoResponse
import com.example.hackathoneonebite.api.Main5LoadPostInfoResponse
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityProfileBinding
import com.example.hackathoneonebite.databinding.DialogMain1CommentBinding
import com.example.hackathoneonebite.databinding.DialogMain5Post0Binding
import com.example.hackathoneonebite.databinding.DialogMain5Post1Binding
import com.example.hackathoneonebite.databinding.DialogMain5Post2Binding
import com.example.hackathoneonebite.databinding.DialogProfileUpdateBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding
import com.example.hackathoneonebite.databinding.ItemMain5PostsBinding
import com.example.hackathoneonebite.main.fragment.AdapterMain1HomeComment
import com.example.hackathoneonebite.main.fragment.Main5ProfileFragment
import com.google.android.material.internal.ViewUtils.dpToPx
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity() {//property
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
    lateinit var binding: ActivityProfileBinding
    var requesterId: Long = 0
    var targetId: Long = 0
    var userId: String = ""
    var isfollowing: Boolean = false
    //network
    val baseUrl: String = "http://203.252.139.231:8080/"
    var refreshingProfile: Boolean = false
    var refreshingPost: Boolean = false
    //network handler
    private val handler = Handler(Looper.getMainLooper())
    private val profileRunnable = Runnable {
        Log.d("MAIN5PROFILE", "프로필 정보를 다시 로드합니다.")
        Toast.makeText(this,"프로필 정보가 로드되지 않았습니다.\n재통신을 시도합니다.", Toast.LENGTH_SHORT).show()
        loadProfileInfo(targetId)
    }
    private val postRunnable = Runnable {
        Log.d("MAIN5PROFILE", "포스트 정보를 다시 로드합니다.")
        Toast.makeText(this,"포스트 정보가 로드되지 않았습니다.\n서버와 재통신을 시도합니다.", Toast.LENGTH_SHORT).show()
        loadPostInfo(targetId)
    }
    //Gallery
    var isProfileImgChangeOrBack: Boolean = true
    var isProfileImgChanged: Boolean = false
    var isBackgroundImgChanged: Boolean = false
    var profileImgName: String = ""
    var backgroundImgName: String= ""
    var profileImageFile: File? = null
    var backgroundImageFile: File? = null
    val REQ_GALLERY = 1
    val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                //서버 업로드를 위해 파일 형태로 변환
                if (isProfileImgChangeOrBack) {
                    profileImageFile = File(getRealPathFromURI(it))
                } else {
                    backgroundImageFile = File(getRealPathFromURI(it))
                }
                //이미지 이름 가져오기
                val cursor = contentResolver.query(imageUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                        if (columnIndex != -1) {
                            if (isProfileImgChangeOrBack) {
                                isProfileImgChanged = true
                                profileImgName = it.getString(columnIndex)
                                if (profileImgName == "") profileImgName = " "
                            } else {
                                isProfileImgChanged = false
                                backgroundImgName = it.getString(columnIndex)
                                if (backgroundImgName == "") backgroundImgName = " "
                            }
                        }
                    }
                }
            }
        }
    }
    //코드 시작
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewTreeObserver = binding.root.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 여기서 뷰의 크기와 위치가 결정된 후의 작업을 수행합니다.
                fragment_width = binding.root.width
                fragment_height = binding.root.height
                profileBackgroundImageHeight = fragment_height - dpToPx(profileInfoViewHeight)
                transientThreshold = (profileBackgroundImageHeight * autoDragRatioThanProfileBackgroundHeight).toInt()
                init()
                initLayout()

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        initScrollview()
    }
    override fun onStart() {
        super.onStart()
        requesterId = intent.getLongExtra("requesterId", 0)
        targetId = intent.getLongExtra("targetId", 0)

        loadProfileInfo(targetId)
        loadPostInfo(targetId)
    }

    override fun onResume() {
        super.onResume()

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
            //재로드
            refreshingProfile = false
            refreshingPost = false
            loadProfileInfo(targetId)
            loadPostInfo(targetId)
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

    //프로필 정보 변경 다이얼로그 표시
    private fun showProfileDialog() {
        isProfileImgChanged = false
        isBackgroundImgChanged = false
        val bindingDialog = DialogProfileUpdateBinding.inflate(layoutInflater)
        //변경 버튼 리스너 등록
        bindingDialog.profileUpdateButton.setOnClickListener {
            if (isProfileImgChanged && isBackgroundImgChanged) { //updateProfile_userId_username_profileImg_backgroundImg

            } else if (isProfileImgChanged) { //updateProfile_userId_username_profileImg

            } else if (isBackgroundImgChanged) { //updateProfile_userId_username_backgroundImg

            } else { //updateProfile_userId_username_profileImg

            }
        }
        //프로필 이미지 변경 버튼 리스너 등록
        bindingDialog.profileImgChangeButton.setOnClickListener {
            profileImgName = ""
            isProfileImgChangeOrBack = true
            selectGallery()
            bindingDialog.profileImgTextView.text = if(profileImgName == "") "재시도 필요" else profileImgName
        }
        //배경 이미지 변경 버튼 리스너 등록
        bindingDialog.backgroundImgChangeButton.setOnClickListener {
            backgroundImgName = ""
            isProfileImgChangeOrBack = false
            selectGallery()
            bindingDialog.backgroundImgTextView.text = if(backgroundImgName == "") "재시도 필요" else backgroundImgName
        }
        val builder = AlertDialog.Builder(this)
        val dlg = builder.setView(bindingDialog.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.BOTTOM)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    //이미지 실제 경로 반환
    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }
    //갤러리 호출하기
    private fun selectGallery() {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        //권한 확인
        if (readPermission == PackageManager.PERMISSION_DENIED) {
            //권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_GALLERY)
        } else {
            //권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            imageResult.launch(intent)
        }
    }
    //프로필 정보 변경 처리
    private fun updateProfile() {

    }

    //프로필 게시물 이미지 추가 함수
    private fun displayPostImage(postList: List<Main5LoadPostInfoResponse>) {
        val gridLayout = binding.profileBottomLayout.gridLayout
        gridLayout.removeAllViews()
        initGridView(postList)
        //아이템 동적 추가
        for (i in 0 until postList.size) {
            val layoutParams = FrameLayout.LayoutParams(itemSize, itemSize)
            val itemBinding = ItemMain5PostsBinding.inflate(layoutInflater)
            Glide.with(this)
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

    //프로필 정보 호출
    private fun profileButtonSet(data_profile: Main5LoadProfileInfoResponse) {
        isfollowing = data_profile.following
        binding.button.visibility = View.VISIBLE
        //버튼 변경
        binding.button.apply {
            if (data_profile.selfProfile) {
                text = ""
                binding.button.visibility = View.INVISIBLE
            } else {
                if (data_profile.following) {
                    text = "팔로잉"
                    setOnClickListener {
                        followToggle(requesterId, targetId, isfollowing)
                    }
                } else {
                    text = "+ 팔로우"
                    setOnClickListener {
                        followToggle(requesterId, targetId, isfollowing)
                    }
                }
            }
        }
    }
    private fun loadProfileInfo(id: Long) {
        Log.d("PROFILE", "id : " + id.toString())
        loadProfileInfoRequest(id, requesterId)
    }
    private fun loadProfileInfoRequest(targetId: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadProfileInfo(targetId, currentId)
        call.enqueue(object : Callback<Main5LoadProfileInfoResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main5LoadProfileInfoResponse>,
                response: Response<Main5LoadProfileInfoResponse>
            ) {
                Log.e("PROFILE: LOAD PROFILE INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val data_profile = response.body()
                    if (data_profile != null) {
                        if (!data_profile!!.isSuccess) {
                            Log.e("PROFILE: LOAD PROFILE INFO1", "서버에서 받은 응답이 잘못됐습니다. success : false")
                            reloadProfile()
                        } else if (data_profile!!.id != targetId) {
                            Log.e("PROFILE: LOAD PROFILE INFO2", "요청한 id와 서버에서 온 정보의 id가 다릅니다.")
                            reloadProfile()
                        } else {
                            refreshingProfile = true
                            loadCompleteDeleteRefreshing()
                            displayResponse(data_profile)
                            profileButtonSet(data_profile)
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
                            Log.e("PROFILE: LOAD PROFILE INFO3", "Failed to parse error response: $errorBody")
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
                Log.d("PROFILE CONNECTION FAILURE: LOAD PROFILE INFO4", t.localizedMessage)
                reloadProfile()
            }
        })
    }
    //포트스 정보 호출
    private fun loadPostInfo(id: Long) {
        loadPostInfoRequest(id, requesterId)
    }
    private fun loadPostInfoRequest(id: Long, currentId: Long) {
        val call = RetrofitBuilder.api.main5LoadPostInfo(id, currentId)
        call.enqueue(object : Callback<List<Main5LoadPostInfoResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<Main5LoadPostInfoResponse>>,
                response: Response<List<Main5LoadPostInfoResponse>>
            ) {
                Log.e("PROFILE: LOAD POST INFO0", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val postList  = response.body()
                    if (postList != null) {
                        refreshingPost = true
                        loadCompleteDeleteRefreshing()
                        displayPostImage(postList)
                    } else {
                        Log.e("PROFILE: LOAD POST INFO1", "응답 성공. 하지만 null입니다.")
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
                            Log.e("PROFILE: LOAD POST INFO2", "Failed to parse error response: $errorBody")
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
                Log.d("PROFILE CONNECTION FAILURE: LOAD POST INFO3", t.localizedMessage)
                reloadPost()            }
        })
    }
    private fun displayResponse(response: Main5LoadProfileInfoResponse) {
        changeProfileImage(baseUrl + response.profileImageUrl)
        changeBackgroundImage(baseUrl + response.backgroundImageUrl)
        binding.username.text = response.username
        binding.userId.text = response.userId
        binding.countRelay.text = response.relayReceivedCount.toString()
        binding.countFollower.text = response.followerIds.count().toString()
        binding.countFollowing.text = response.followingIds.count().toString()
    }
    private fun changeProfileImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.profileMainImage)
    }
    private fun changeBackgroundImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.profileBackgroundImage)
    }
    private fun reloadProfile() {
        handler.postDelayed(profileRunnable, 7000)
    }
    private fun reloadPost() {
        handler.postDelayed(postRunnable, 7000)
    }
    //팔로우
    private fun applyFollowToggleResponse(wasFollowed: Boolean) {
        if (wasFollowed) {
            isfollowing = false
            binding.button.text = "+ 팔로우"
        } else {
            isfollowing = true
            binding.button.text = "팔로잉"
        }
        loadProfileInfo(targetId)
    }
    private fun followToggle(requesterId: Long, targetId: Long, wasFollowed: Boolean) {
        followToggleRequest(requesterId, targetId, wasFollowed)
    }
    private fun followToggleRequest(requesterId: Long, targetId: Long, wasFollowed: Boolean) {
        val call = RetrofitBuilder.api.followToggleRequest(requesterId, targetId)
        call.enqueue(object : Callback<FollowToggleResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<FollowToggleResponse>,
                response: Response<FollowToggleResponse>
            ) {
                Log.e("PROFILE: FOLLOW TOGGLE1", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val data_follow = response.body()
                    if (data_follow != null) {
                        if (data_follow.isSuccess){
                            if (data_follow.isFollowing == !wasFollowed) {
                                applyFollowToggleResponse(wasFollowed)
                            } else {
                                Log.e("PROFILE: FOLLOW TOGGLE0", "success: True. 그러나 팔로우 팔로잉이 서버와 맞지 않습니다.")
                            }
                        } else {
                            Log.e("PROFILE: FOLLOW TOGGLE1", "응답 성공. 하지만 변경에 실패했습니다.")
                        }
                    } else {
                        Log.e("PROFILE: FOLLOW TOGGLE2", "응답 성공. 하지만 null입니다.")
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
                            reloadProfile()
                        } catch (e: JSONException) {
                            Log.e("PROFILE: FOLLOW TOGGLE3", "Failed to parse error response: $errorBody")
                            //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            reloadProfile()
                        }
                    } else {
                        //Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        reloadProfile()
                    }
                }
            }
            override fun onFailure(call: Call<FollowToggleResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("PROFILE: FOLLOW TOGGLE4", t.localizedMessage)
                reloadProfile()
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
        val builder = AlertDialog.Builder(this)
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
        } else if (aPostInfo.participantUserIdStrings.count() == 4) {
            bindingCommentDialog.userIdStringTextView1.text = aPostInfo.participantUserIdStrings[0]
            bindingCommentDialog.userIdStringTextView2.text = aPostInfo.participantUserIdStrings[1]
            bindingCommentDialog.userIdStringTextView3.text = aPostInfo.participantUserIdStrings[2]
            bindingCommentDialog.userIdStringTextView4.text = aPostInfo.participantUserIdStrings[3]
        }
        bindingCommentDialog.textTextView.text = aPostInfo.text
        bindingCommentDialog.dateTextView.text = calculateTimeDifference(aPostInfo.date)
        bindingCommentDialog.addCommentButton.setOnClickListener {
            createComment(aPostInfo.id,this.requesterId,bindingCommentDialog.addCommentEditTextView.text.toString())
        }
    }
    private fun initCommentRecyclerView() {
        bindingCommentDialog.apply {
            //리사이클러뷰 생성
            commentRecyclerView.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, false)
            adapter_comment = AdapterMain1HomeComment(this@ProfileActivity)
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
        val i = Intent(this@ProfileActivity, ProfileActivity::class.java)
        i.putExtra("requesterId",this.requesterId)
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
                                val builder = AlertDialog.Builder(this@ProfileActivity)
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1frame1)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2frame1)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3frame1)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4frame1)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = aPostInfo.date.split('T')[0]
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
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
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
                                val builder = AlertDialog.Builder(this@ProfileActivity)
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1frame2)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2frame2)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3frame2)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4frame2)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = aPostInfo.date.split('T')[0]
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
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
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
                                val builder = AlertDialog.Builder(this@ProfileActivity)
                                val dlg = builder.setView(bindingAPost.root).show()
                                dlg.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dlg.window?.setGravity(Gravity.CENTER)
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                //프레임 이미지 변경
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[0])
                                    .into(bindingAPost.postImageLayout.imageView1)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[1])
                                    .into(bindingAPost.postImageLayout.imageView2)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[2])
                                    .into(bindingAPost.postImageLayout.imageView3)
                                Glide.with(this@ProfileActivity)
                                    .load(baseUrl + aPostInfo.imageArray[3])
                                    .into(bindingAPost.postImageLayout.imageView4)
                                //날짜 변경
                                bindingAPost.postImageLayout.dateTextView.text = aPostInfo.date.split('T')[0]
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
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    bindingAPost.userProfileImageView1.visibility = View.VISIBLE
                                    bindingAPost.userProfileImageView2.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView3.visibility = View.INVISIBLE
                                    bindingAPost.userProfileImageView4.visibility = View.INVISIBLE
                                } else if (aPostInfo.participantsUserProfileUrl.count() == 4) {
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[0])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[1])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
                                        .load(baseUrl + aPostInfo.participantsUserProfileUrl[2])
                                        .into(bindingAPost.userProfileImageView1)
                                    Glide.with(this@ProfileActivity)
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
                            Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_COMMENT25", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LoadAPostInfoResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_COMMENT26: ", t.localizedMessage)
                Toast.makeText(this@ProfileActivity, "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_COMMENT35", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_COMMENT36: ", t.localizedMessage)
                Toast.makeText(this@ProfileActivity, "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
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
                        hideKeyboard(this@ProfileActivity)
                        loadComments(postId)
                    }
                } else {
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME_CREATE_COMMENT43", "Failed to parse error response: $errorBody")
                            Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_CREATE_COMMENT44: ", t.localizedMessage)
                Toast.makeText(this@ProfileActivity, "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //</editor-fold>
}