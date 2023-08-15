package com.example.hackathoneonebite.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.setMargins
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.FollowToggleResponse
import com.example.hackathoneonebite.api.Main5LoadPostInfoResponse
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.ActivityProfileBinding
import com.example.hackathoneonebite.databinding.DialogMain5PostBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding
import com.example.hackathoneonebite.databinding.ItemMain5PostsBinding
import com.example.hackathoneonebite.main.fragment.Main5ProfileFragment
import com.google.android.material.internal.ViewUtils.dpToPx
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    //network
    val baseUrl: String = "http://221.146.39.177:8081/"
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
                showPostDialog()
            }

            gridLayout.addView(view)
        }
    }

    private fun showPostDialog() {
        val bindingDialog = DialogMain5PostBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        val dlg = builder.setView(bindingDialog.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.BOTTOM)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    //프로필 정보 호출
    private fun profileButtonSet(data_profile: Main5LoadProfileInfoResponse) {
        //버튼 변경
        binding.button.apply {
            if (data_profile.selfProfile) {
                text = "수정"
                setOnClickListener {

                }
            } else {
                if (data_profile.following) {
                    text = "- 팔로잉"
                    setOnClickListener {
                        followToggle(requesterId, targetId, true)
                    }
                } else {
                    text = "+ 팔로우"
                    setOnClickListener {
                        followToggle(requesterId, targetId, false)
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
            binding.button.text = "+ 팔로우"
        } else {
            binding.button.text = "팔로잉"
        }
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
                            applyFollowToggleResponse(wasFollowed)
                        } else {
                            Log.e("PROFILE: FOLLOW TOGGLE2", "응답 성공. 하지만 변경에 실패했습니다.")
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
}