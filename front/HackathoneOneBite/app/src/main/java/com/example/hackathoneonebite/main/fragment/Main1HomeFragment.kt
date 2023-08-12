package com.example.hackathoneonebite.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.input.key.Key.Companion.Copy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.TypedArrayUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main1LoadPostRequest
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.DialogMain1TopBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeThema1Binding
import com.example.hackathoneonebite.main.MainFrameActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME


class Main1HomeFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeBinding
    lateinit private var adapter_thema1: AdapterMain1HomeThema1
    lateinit private var adapter_thema2: AdapterMain1HomeThema2
    lateinit private var adapter_film: AdapterMain1HomeFilm
    var userId: Long = 0
    val baseUrl = "http://221.146.39.177:8081/"
    val data_thema1: ArrayList<Post> = ArrayList()
    val data_thema2: ArrayList<Post> = ArrayList()
    val data_film: ArrayList<Post> = ArrayList()
    var fragment_width: Int = 0
    var fragment_height: Int = 0

    //music
    lateinit var musicArray: TypedArray
    lateinit var musicNameArray: Array<String>
    lateinit var singerArray: Array<String>
    var mediaPlayer: MediaPlayer? = null

    enum class ThemaNumbering(val value: Int) {
        thema1(0),
        thema2(1),
        film(2)
    }

    var current_selected_thema: Int = ThemaNumbering.thema1.value

    override fun onAttach(context: Context) {
        //Log.d("onAttach", "onAttach called");
        super.onAttach(context)
        val activity = requireActivity() as MainFrameActivity
        this.userId = activity.id
        Log.d("ID", "메인 Fragment Attached / ID : ${this.id}")
        musicArray = resources.obtainTypedArray(R.array.music_array)
        initData(data_thema1)
        initData(data_thema2)
        initData(data_film)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain1HomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            fragment_width = view.width
            fragment_height = view.height
            initFilmRecyclerView()
        }

        //arrays.xml정보 가져오기
        musicNameArray = resources.getStringArray(R.array.music_name)
        singerArray = resources.getStringArray(R.array.singer)

        initSelectedThema()
        initRecyclerView()
        init()
    }

    private fun initData(data: ArrayList<Post>) { //백엔드와 연결 전 단계에 테스트를 위해 데이터 생성하는 함수
        for(i in 0..100) {
            data.add(Post())
            data[i].id = arrayListOf(1,2,3,4)
            data[i].likeCount = 10
            data[i].date = LocalDateTime.now()
            data[i].message = i.toString()+ "하하하하하하하" + i.toString() + "히히히히히히히" + i.toString() + "헤헤헤헤헤헤헤" + i.toString() + i.toString() + "키키키키키키키" + i.toString() + i.toString() + i.toString() + i.toString()
            data[i].musicNum = i % musicArray.length()
            for (j in 0..3) {
                if(j == i % 4) {
                    data[i].imgArray[j] = R.drawable.test_image1.toString()
                } else {
                    data[i].imgArray[j] = R.drawable.test_image2.toString()
                }
            }
        }
    }

    private fun initSelectedThema() {
        val themaNum = arguments?.getInt("themaNum")

        if (themaNum != null) {
            current_selected_thema = themaNum
            when(themaNum) {
                0 -> {
                    binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    loadPosts(0)
                }
                1 -> {
                    binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    loadPosts(1)
                }
                2 -> {
                    binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    loadPosts(2)
                }
            }
        }
    }

    private fun init() { //이벤트 리스너 초기화
        binding.changeThemaButton.setOnClickListener {
            showThemaSelectDialog()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() { //리사이클러뷰 초기화
        //Thema1 RecyclerView
        binding.postImageLayoutThema1.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter_thema1 = AdapterMain1HomeThema1(requireContext(), data_thema1)
            adapter_thema1.itemClickListener = object: AdapterMain1HomeThema1.OnItemClickListener {
                override fun OnItemClick(position: Int) {
                    data_thema1[position].isFliped = !data_thema1[position].isFliped
                    if (data_thema1[position].isFliped) {
                        try {
                            val musicNum = data_thema1[position].musicNum
                            musicNameTextView.text = musicNameArray[musicNum]
                            singerNameTextView.text = singerArray[musicNum]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            Log.e("ExampleFragment", "없는 인덱스 곡입니다. : ${e.message}")
                        }
                        messageTextView.visibility = View.INVISIBLE
                        likeButton.visibility = View.INVISIBLE
                        shareButton.visibility = View.INVISIBLE
                        musicNameTextView.visibility = View.VISIBLE
                        singerNameTextView.visibility = View.VISIBLE
                    } else {
                        messageTextView.visibility = View.VISIBLE
                        likeButton.visibility = View.VISIBLE
                        shareButton.visibility = View.VISIBLE
                        musicNameTextView.visibility = View.INVISIBLE
                        singerNameTextView.visibility = View.INVISIBLE
                    }
                }
            }
            //음악 재생 버튼 클릭 시
            adapter_thema1.musicPlayButtonClickListener = object: AdapterMain1HomeThema1.MusicPlayButtonClickListener{
                override fun OnItemClick(position: Int, isMusicPlaying: Boolean, musicNum: Int) {
                    if (isMusicPlaying) {
                        if(mediaPlayer==null){
                            val musicResourceID = musicArray.getResourceId(musicNum, -1)
                            if (musicResourceID != -1) {
                                mediaPlayer = MediaPlayer.create(requireContext(), musicResourceID)
                                mediaPlayer?.setVolume(1f, 1f)
                            } else {
                                Log.e("Main1Home", "해당하는 음악이 없습니다.")
                            }
                        }
                        mediaPlayer?.start()
                        Log.d("Music", "음악 재생!")
                    } else {
                        mediaPlayer?.pause()
                        Log.d("Music", "음악 일시정지!")
                    }
                }
            }
            recyclerView.adapter = adapter_thema1

            //TODO: 나중에 백엔드 연결했을 때 수정해야됨.
            //TODO: 다른 리사이클러뷰(다른 테마)로 변경할 때 데이터가 없어도 VISIBLE로 됨. 이것도 고려해야됨.
            if (data_thema1.isNotEmpty()) {
                messageTextView.text = data_thema1[0]?.message
            } else {
                viewGroup.visibility = View.INVISIBLE
            }

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val centerView = snapHelper.findSnapView(layoutManager)
                        val pos = layoutManager.getPosition(centerView!!)
                        messageTextView.text = data_thema1[pos]?.message
                        if (data_thema1[pos].isFliped) {
                            try {
                                val musicNum = data_thema1[pos].musicNum
                                musicNameTextView.text = musicNameArray[musicNum]
                                singerNameTextView.text = singerArray[musicNum]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                Log.e("ExampleFragment", "없는 인덱스 곡입니다. : ${e.message}")
                            }
                            messageTextView.visibility = View.INVISIBLE
                            likeButton.visibility = View.INVISIBLE
                            shareButton.visibility = View.INVISIBLE
                            musicNameTextView.visibility = View.VISIBLE
                            singerNameTextView.visibility = View.VISIBLE
                        } else {
                            Log.d("position",pos.toString())
                            messageTextView.visibility = View.VISIBLE
                            likeButton.visibility = View.VISIBLE
                            shareButton.visibility = View.VISIBLE
                            musicNameTextView.visibility = View.INVISIBLE
                            singerNameTextView.visibility = View.INVISIBLE
                        }

                        //music play
                        //centerView.findViewById<ConstraintLayout>(R.id.postImageLayoutBack).findViewById<Button>(R.id.playButton).performClick()

                        //music stop
                        val playingViewHolder = adapter_thema1.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolder.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager

                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                Log.d("Music", "음악 정지!")
                                mediaPlayer = null
                                playingViewHolder.stopMusicAnimation()
                                adapter_thema1.currentlyPlayingViewHolder = null
                            }
                        }
                    }
                }
            })
        }
        //Thema2 RecyclerView
        binding.postImageLayoutThema2.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter_thema2 = AdapterMain1HomeThema2(data_thema2)
            adapter_thema2.itemClickListener = object: AdapterMain1HomeThema2.OnItemClickListener {
                override fun OnItemClick(position: Int) {
                    data_thema2[position].isFliped = !data_thema2[position].isFliped
                    if (data_thema2[position].isFliped) {
                        try {
                            val musicNum = data_thema2[position].musicNum
                            musicNameTextView.text = musicNameArray[musicNum]
                            singerNameTextView.text = singerArray[musicNum]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            Log.e("ExampleFragment", "없는 인덱스 곡입니다. : ${e.message}")
                        }
                        messageTextView.visibility = View.INVISIBLE
                        likeButton.visibility = View.INVISIBLE
                        shareButton.visibility = View.INVISIBLE
                        musicNameTextView.visibility = View.VISIBLE
                        singerNameTextView.visibility = View.VISIBLE
                    } else {
                        messageTextView.visibility = View.VISIBLE
                        likeButton.visibility = View.VISIBLE
                        shareButton.visibility = View.VISIBLE
                        musicNameTextView.visibility = View.INVISIBLE
                        singerNameTextView.visibility = View.INVISIBLE
                    }
                }
            }
            //음악 재생 버튼 클릭 시
            adapter_thema2.musicPlayButtonClickListener = object: AdapterMain1HomeThema2.MusicPlayButtonClickListener{
                override fun OnItemClick(position: Int, isMusicPlaying: Boolean, musicNum: Int) {
                    if (isMusicPlaying) {
                        if(mediaPlayer==null){
                            val musicResourceID = musicArray.getResourceId(musicNum, -1)
                            if (musicResourceID != -1) {
                                mediaPlayer = MediaPlayer.create(requireContext(), musicResourceID)
                                mediaPlayer?.setVolume(1f, 1f)
                            } else {
                                Log.e("Main1Home", "해당하는 음악이 없습니다.")
                            }
                        }
                        mediaPlayer?.start()
                        Log.d("Music", "음악 재생!")
                    } else {
                        mediaPlayer?.pause()
                        Log.d("Music", "음악 일시정지!")
                    }
                }
            }
            recyclerView.adapter = adapter_thema2

            //TODO: 나중에 백엔드 연결했을 때 수정해야됨.
            //TODO: 다른 리사이클러뷰(다른 테마)로 변경할 때 데이터가 없어도 VISIBLE로 됨. 이것도 고려해야됨.
            if (data_thema2.isNotEmpty()) {
                messageTextView.text = data_thema2[0]?.message
            } else {
                viewGroup.visibility = View.INVISIBLE
            }

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val centerView = snapHelper.findSnapView(layoutManager)
                        val pos = layoutManager.getPosition(centerView!!)
                        messageTextView.text = data_thema2[pos]?.message
                        if (data_thema2[pos].isFliped) {
                            try {
                                val musicNum = data_thema2[pos].musicNum
                                musicNameTextView.text = musicNameArray[musicNum]
                                singerNameTextView.text = singerArray[musicNum]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                Log.e("ExampleFragment", "없는 인덱스 곡입니다. : ${e.message}")
                            }
                            messageTextView.visibility = View.INVISIBLE
                            likeButton.visibility = View.INVISIBLE
                            shareButton.visibility = View.INVISIBLE
                            musicNameTextView.visibility = View.VISIBLE
                            singerNameTextView.visibility = View.VISIBLE
                        } else {
                            Log.d("position",pos.toString())
                            messageTextView.visibility = View.VISIBLE
                            likeButton.visibility = View.VISIBLE
                            shareButton.visibility = View.VISIBLE
                            musicNameTextView.visibility = View.INVISIBLE
                            singerNameTextView.visibility = View.INVISIBLE
                        }

                        //music stop
                        val playingViewHolder = adapter_thema2.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolder.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager

                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                Log.d("Music", "음악 정지!")
                                mediaPlayer = null
                                playingViewHolder.stopMusicAnimation()
                                adapter_thema2.currentlyPlayingViewHolder = null
                            }
                        }
                    }
                }
            })
        }
    }

    private fun initFilmRecyclerView() {
        //Film RecyclerView
        binding.postImageLayoutFilm.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter_film = AdapterMain1HomeFilm(data_film)
            adapter_film.width = fragment_width
            adapter_film.height = ((fragment_height - 105) * 0.64).toInt()
            adapter_film.itemClickListener = object: AdapterMain1HomeFilm.OnItemClickListener {
                override fun OnItemClick(position: Int) {
                    data_film[position].isFliped = !data_film[position].isFliped
                }
            }
            recyclerView.adapter = adapter_film

            //TODO: 나중에 백엔드 연결했을 때 수정해야됨.
            //TODO: 다른 리사이클러뷰(다른 테마)로 변경할 때 데이터가 없어도 VISIBLE로 됨. 이것도 고려해야됨.
            if (data_film.isNotEmpty()) {
                messageTextView.text = data_film[0]?.message
            } else {
                viewGroup.visibility = View.INVISIBLE
            }
            //리사이클러뷰 터치 리스터
            var totalDy: Int = 0
            var isTouching: Boolean = false
            val touchListener = object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            totalDy = 0
                            isTouching = true
                            Log.e("eee", totalDy.toString())
                        }
                        MotionEvent.ACTION_UP -> {
                            // 사용자가 아이템 터치를 끝냈을 때 수행할 동작
                            isTouching = false
                        }
                    }
                    return super.onInterceptTouchEvent(rv, e)
                }
            }
            recyclerView.addOnItemTouchListener(touchListener)
            //처음 아이템이 조금만 보이도록 item간에 간격 조정
            val thresholdDistance = adapter_film.height * 0.4f
            var itemDecorationApplied = true
            val itemDecoration = object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == 0) {
                        outRect.bottom = (recyclerView.height * 0.88f).toInt()
                    }
                }
            }
            recyclerView.addItemDecoration(itemDecoration)
            //아이템들이 중앙에 오도록
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val centerView = snapHelper.findSnapView(layoutManager)
                        val pos = layoutManager.getPosition(centerView!!)
                        messageTextView.text = data_film[pos]?.message
                    }
                }
            })
            //스크롤 밝기 조절
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    adjustBrightness(recyclerView)

                    //첫 항목 스크롤
                    if(itemDecorationApplied) {
                        totalDy -= dy
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val centerView = snapHelper.findSnapView(layoutManager)
                        val pos = layoutManager.getPosition(centerView!!)

                        if (pos == 0) {
                            if (totalDy > thresholdDistance) {
                                itemDecorationApplied = false
                                recyclerView.removeItemDecoration(itemDecoration)
                                recyclerView.scrollBy(0, (recyclerView.height * 0.88f).toInt())
                                totalDy = 0
                            } else if (totalDy > 0 && !isTouching) {
                                layoutManager.scrollToPositionWithOffset(0, 0)
                                totalDy = 0
                            }
                        }
                    }
                }
            })
        }
    }
    //스크롤 밝기 조절을 위한 함수
    private fun adjustBrightness(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val itemView = layoutManager.findViewByPosition(i)

            //투명도 계산 부분
            val distance = Math.abs(itemView!!.bottom - recyclerView.height)
            val maxDistance = recyclerView.height * 0.7f
            val alpha: Float
            if(distance > maxDistance)
                alpha = 0f
            else
                alpha = 1 - ((distance - 0.3f) / maxDistance)

            val filmLayout:ConstraintLayout = itemView.findViewById(R.id.postImageLayout)
            filmLayout.findViewById<ImageView>(R.id.imageView1).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView2).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView3).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView4).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.RELAYImageView).alpha = alpha
            filmLayout.findViewById<TextView>(R.id.dateTextView).alpha = alpha
        }
    }

    //아예 안보이게 될 때를 체크
    fun RecyclerView.LayoutManager.isViewCompletelyVisible(position: Int): Boolean {
        val view = this.findViewByPosition(position)
        return view != null && !this.isViewPartiallyVisible(view, true, true)
    }


    //어떤 테마를 모아볼 지 선택하는 Dialog
    fun showThemaSelectDialog() {
        val bindingDialog = DialogMain1TopBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        val dlg = builder.setView(bindingDialog.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.TOP)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDialog.closeButton.setOnClickListener {
            dlg.dismiss()
        }

        //Dialog에서 thema1을 선택했을 경우
        bindingDialog.postFrame1.setOnClickListener {
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    dlg.dismiss()
                }

                ThemaNumbering.thema2.value -> {
                    binding.postImageLayoutThema2.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.thema1.value
                    dlg.dismiss()
                }

                ThemaNumbering.film.value -> {
                    binding.postImageLayoutFilm.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.thema1.value
                    dlg.dismiss()
                }
            }
        }
        //Dialog에서 thema2을 선택했을 경우
        bindingDialog.postFrame2.setOnClickListener {
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    binding.postImageLayoutThema1.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.thema2.value
                    dlg.dismiss()
                }

                ThemaNumbering.thema2.value -> {
                    dlg.dismiss()
                }

                ThemaNumbering.film.value -> {
                    binding.postImageLayoutFilm.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.thema2.value
                    dlg.dismiss()
                }
            }
        }
        //Dialog에서 Film을 선택했을 경우
        bindingDialog.postFrameFilm.setOnClickListener {
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    binding.postImageLayoutThema1.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.film.value
                    dlg.dismiss()
                }

                ThemaNumbering.thema2.value -> {
                    binding.postImageLayoutThema2.viewGroup.visibility = View.INVISIBLE
                    binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    current_selected_thema = ThemaNumbering.film.value
                    dlg.dismiss()
                }

                ThemaNumbering.film.value -> {
                    dlg.dismiss()
                }
            }
        }
    }

    //네에에에트으으으으으워어어어어크으으으으으으
    private fun loadPosts(themaNum: Int) {
        //TODO: 통신시 이거 주석해놓은거 없애야 함.
        //loadPostRequest(this.userId, themaNum)
    }
    fun loadPostRequest(userId: Long, theme: Int){
        val call = RetrofitBuilder.api.main1LoadPostRequest(0,0,"eeee",20)
        call.enqueue(object : Callback<Main1LoadPostResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<Main1LoadPostResponse>,
                response: Response<Main1LoadPostResponse>
            ) {
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val userResponse = response.body()
                    if (userResponse == null)
                        return
                    // userResponse를 사용하여 JSON 데이터에 접근할 수 있습니다.
                    Log.d("RESPONSE: ", "응답 성공")

                    val post = Post()
                    post.id = userResponse.postId
                    post.imgArray[0] = baseUrl + userResponse.images[0]
                    post.theme = userResponse.theme
                    post.likeCount = userResponse.likeCount
                    post.date = LocalDateTime.parse(userResponse.date, ISO_LOCAL_DATE_TIME)
                    post.message = userResponse.text
                    post.musicNum = userResponse.musicNum

                    when(theme) {
                        0 -> {
                            data_thema1.add(post)
                            adapter_thema1.notifyDataSetChanged()
                        }
                        1 -> {
                            data_thema2.add(post)
                            adapter_thema2.notifyDataSetChanged()
                        }
                        2 -> {
                            data_film.add(post)
                            adapter_film.notifyDataSetChanged()
                        }
                    }

                    Toast.makeText(requireContext(),userResponse?.date.toString(), Toast.LENGTH_LONG).show()
                }else{
                    // 통신 성공 but 응답 실패
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val errorMessage = jsonObject.getString("error_message")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Log.e("ERROR PARSING", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<Main1LoadPostResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

/*//리사이클러뷰의 아이템간의 간격 설정을 위한 클래스
class CenterItemDecoration(private val itemHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        if (itemPosition == 0) {
            initializeItemPosition(outRect, parent)
        } else {
            ensureItemPosition(outRect, parent)
        }
    }

    private fun initializeItemPosition(outRect: Rect, parent: RecyclerView) {
        val totalWidth = parent.width
        //outRect.bottom = (itemWidth * 0.88).toInt()
        //outRect.top = (-0.18 * itemHeight).toInt()
    }

    private fun ensureItemPosition(outRect: Rect, parent: RecyclerView) {
        val totalWidth = parent.width
        //outRect.bottom = (-0.18 * itemHeight).toInt()
        //outRect.top = (-0.18 * itemHeight).toInt()
    }
}*/