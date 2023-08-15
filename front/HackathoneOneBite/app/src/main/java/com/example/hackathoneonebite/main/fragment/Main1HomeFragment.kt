package com.example.hackathoneonebite.main.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.StartActivity
import com.example.hackathoneonebite.api.CommentResponse
import com.example.hackathoneonebite.api.CreateComment
import com.example.hackathoneonebite.api.LikeClickResponse
import com.example.hackathoneonebite.api.Main1LoadPostResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import com.example.hackathoneonebite.databinding.DialogMain1CommentBinding
import com.example.hackathoneonebite.databinding.DialogMain1TopBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeFilmBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeThema1Binding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeThema2Binding
import com.example.hackathoneonebite.main.MainFrameActivity
import com.example.hackathoneonebite.main.NotificationActivity
import com.example.hackathoneonebite.main.ProfileActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class Main1HomeFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeBinding
    lateinit var thema1Binding: FragmentMain1HomeThema1Binding
    lateinit var thema2Binding: FragmentMain1HomeThema2Binding
    lateinit var filmBinding: FragmentMain1HomeFilmBinding
    lateinit var commentDialogBinding: DialogMain1CommentBinding
    lateinit private var adapter_thema1: AdapterMain1HomeThema1
    lateinit private var adapter_thema2: AdapterMain1HomeThema2
    lateinit private var adapter_film: AdapterMain1HomeFilm
    lateinit var adapter_comment: AdapterMain1HomeComment
    var userId: String = ""
    var id: Long = 0
    val baseUrl = "http://221.146.39.177:8081/"
    val data_thema1: ArrayList<Post> = ArrayList()
    val data_thema2: ArrayList<Post> = ArrayList()
    val data_film: ArrayList<Post> = ArrayList()
    var fragment_width: Int = 0
    var fragment_height: Int = 0

    //network handler
    private val handler = Handler(Looper.getMainLooper())
    private var currentRequestedTheme: Int = -1
    private val loadMainRunnable = Runnable {
        Log.d("MAIN1HOME", "theme ${currentRequestedTheme}를 다시 로드합니다.")
        Toast.makeText(requireContext(),"게시물이 로드되지 않았습니다.\n재통신을 시도합니다.", Toast.LENGTH_SHORT).show()
        loadPosts(currentRequestedTheme)
    }

    //music
    lateinit var musicArray: TypedArray
    lateinit var musicNameArray: Array<String>
    lateinit var singerArray: Array<String>
    var playingViewHolderTheme1: AdapterMain1HomeThema1.ViewHolder? = null
    var playingViewHolderTheme2: AdapterMain1HomeThema2.ViewHolder? = null
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
        this.id = activity.id
        this.userId = activity.userId
        currentRequestedTheme = arguments?.getInt("themaNum") ?: 0
        Log.d("ID", "메인 Fragment Attached / ID : ${this.id}")
        musicArray = resources.obtainTypedArray(R.array.music_array)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain1HomeBinding.inflate(layoutInflater, container, false)
        thema1Binding = FragmentMain1HomeThema1Binding.inflate(layoutInflater)
        thema2Binding = FragmentMain1HomeThema2Binding.inflate(layoutInflater)
        filmBinding = FragmentMain1HomeFilmBinding.inflate(layoutInflater)
        commentDialogBinding = DialogMain1CommentBinding.inflate(layoutInflater)
        currentRequestedTheme = -1
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
        initCommentRecyclerView()
        initNotification()
        initLikeButton()
        init()
    }

    private fun initNotification() {
        binding.notificationButton.setOnClickListener{
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        //글 로드 중지
        handler.removeCallbacks(loadMainRunnable)
        //음악 중지
        mediaPlayer?.stop()
        mediaPlayer?.release()
        Log.d("MAIN1HOME_MUSIC", "음악 정지!")
        mediaPlayer = null
        playingViewHolderTheme1?.stopMusicAnimation() ?: Log.d("MAIN1HOME_MUSIC","it's null")
        playingViewHolderTheme1 = null
        adapter_thema1.currentlyPlayingViewHolder = null
    }

    private fun initSelectedThema() {
        if (currentRequestedTheme == -1) {
            currentRequestedTheme = arguments?.getInt("themaNum") ?: 0
        }

        if (currentRequestedTheme != null) {
            when(currentRequestedTheme) {
                0 -> {
                    Log.d("MAIN1HOME_LIKE", current_selected_thema.toString())
                    binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    loadPosts(0)
                }
                1 -> {
                    Log.d("MAIN1HOME_LIKE", current_selected_thema.toString())
                    binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    loadPosts(1)
                }
                2 -> {
                    Log.d("MAIN1HOME_LIKE", current_selected_thema.toString())
                    binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    loadPosts(2)
                }
                else -> {
                    Log.e("MAIN1HOME_UNBELIEVEABLE_ERROR1", "예상치 못한 버그 이게 보인다면 다시 코드 읽으시길.. ㅎㅎ")
                }
            }
        }
    }
    private fun init() { //이벤트 리스너 초기화
        binding.changeThemaButton.setOnClickListener {
            showThemaSelectDialog()
        }
        binding.postImageLayoutThema1.messageTextView.setOnClickListener {
            val layoutManager = binding.postImageLayoutThema1.recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            adapter_comment.data.clear()
            Log.d("MAIN1COMMENT",  data_thema1[firstVisiblePosition].toString())
            loadComments(data_thema1[firstVisiblePosition].postId, firstVisiblePosition)
            setPostDataInCommentWindow(firstVisiblePosition)
            showCommentDialog()
        }
        binding.postImageLayoutThema2.messageTextView.setOnClickListener {
            val layoutManager = binding.postImageLayoutThema2.recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            adapter_comment.data.clear()
            Log.d("MAIN1COMMENT",  data_thema2[firstVisiblePosition].toString())
            loadComments(data_thema2[firstVisiblePosition].postId, firstVisiblePosition)
            setPostDataInCommentWindow(firstVisiblePosition)
            showCommentDialog()
        }
        binding.postImageLayoutFilm.messageTextView.setOnClickListener {
            val layoutManager = binding.postImageLayoutFilm.recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            adapter_comment.data.clear()
            loadComments(data_film[firstVisiblePosition].postId, firstVisiblePosition)
            setPostDataInCommentWindow(firstVisiblePosition)
            showCommentDialog()
        }
        commentDialogBinding.addCommentButton.setOnClickListener {
            when (currentRequestedTheme) {
                0 -> {
                    val layoutManager = binding.postImageLayoutThema1.recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    createComment(data_thema1[position].postId, id, commentDialogBinding.addCommentEditTextView.text.toString())
                }
                1 -> {
                    val layoutManager = binding.postImageLayoutThema2.recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    createComment(data_thema2[position].postId, id, commentDialogBinding.addCommentEditTextView.text.toString())
                }
                2 -> {
                    val layoutManager = binding.postImageLayoutFilm.recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    createComment(data_film[position].postId, id, commentDialogBinding.addCommentEditTextView.text.toString())
                }
            }
        }
    }
//<editor-fold desc="좋아요 뷰 접어놓기">
    private fun initLikeButton() {
        binding.postImageLayoutThema1.likeButton.setOnClickListener {
            val layoutManager = binding.postImageLayoutThema1.recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            val postId = data_thema1[position].postId
            val beforeStatus = data_thema1[position].likeClicked
            likeClick(postId, id, beforeStatus, current_selected_thema, position)
        }
        binding.postImageLayoutThema2.likeButton.setOnClickListener {
            val layoutManager = binding.postImageLayoutThema2.recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            val postId = data_thema2[position].postId
            val beforeStatus = data_thema2[position].likeClicked
            likeClick(postId, id, beforeStatus, current_selected_thema, position)
        }
        binding.postImageLayoutFilm.likeButton.setOnClickListener {
            val layoutManager = binding.postImageLayoutFilm.recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            val postId = data_film[position].postId
            val beforeStatus = data_film[position].likeClicked

            likeClick(postId, id, beforeStatus, current_selected_thema, position)
        }
    }
    private fun likeClick(postId: Long, userId: Long, beforeStatus: Boolean, thema: Int, pos: Int) {
        likeClickRequest(postId, userId, beforeStatus, thema, pos)
    }
    private fun likeClickRequest(postId: Long, userId: Long, beforeStatus: Boolean, thema: Int, pos: Int) {
        val call = RetrofitBuilder.api.likeClickRequest(postId, userId)
        call.enqueue(object : Callback<LikeClickResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<LikeClickResponse>,
                response: Response<LikeClickResponse>
            ) {
                Log.e("MAIN1HOME_LIKE", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val likeClickResponse  = response.body()
                    if (likeClickResponse == null) {
                        Log.e("MAIN1HOME_LIKE", "응답이 null입니다.")
                        return
                    } else {
                        Log.d("MAIN1HOME_LIKE", "응답 좋음.")
                        when (thema) {
                            0 -> {
                                val wasliked: Boolean = data_thema1[pos].likeClicked
                                data_thema1[pos].likeClicked = !wasliked
                                if (!wasliked) {
                                    //pop up 랜덤 출력
                                    val randomValue = Random.nextInt(2)
                                    val resourceImaga: Int =
                                        if (randomValue == 0) R.drawable.img_pop_up_smile else R.drawable.img_pop_up_heart
                                    val imageView = binding.likePopUpImageView
                                    imageView.setImageResource(resourceImaga)
                                    imageView.visibility = View.VISIBLE
                                    if (!wasliked) {
                                        val scaleUpAnimation = AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.scale_up
                                        )
                                        imageView.startAnimation(scaleUpAnimation)
                                    }
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        imageView.visibility = View.GONE
                                    }, 800)
                                }
                                //버튼 리소스 변경
                                binding.postImageLayoutThema1.likeButton.setBackgroundResource(
                                    if (wasliked)
                                        R.drawable.img_icon_like_unclick
                                    else
                                        R.drawable.img_icon_like_click
                                )
                            }
                            1 -> {
                                val wasliked: Boolean = data_thema2[pos].likeClicked
                                data_thema2[pos].likeClicked = !wasliked
                                if (!wasliked) {
                                    //pop up 랜덤 출력
                                    val randomValue = Random.nextInt(2)
                                    val resourceImaga: Int =
                                        if (randomValue == 0) R.drawable.img_pop_up_smile else R.drawable.img_pop_up_heart
                                    val imageView = binding.likePopUpImageView
                                    imageView.setImageResource(resourceImaga)
                                    imageView.visibility = View.VISIBLE
                                    if (!wasliked) {
                                        val scaleUpAnimation = AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.scale_up
                                        )
                                        imageView.startAnimation(scaleUpAnimation)
                                    }
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        imageView.visibility = View.GONE
                                    }, 800)
                                }
                                //버튼 리소스 변경
                                binding.postImageLayoutThema2.likeButton.setBackgroundResource(
                                    if (wasliked)
                                        R.drawable.img_icon_like_unclick
                                    else
                                        R.drawable.img_icon_like_click
                                )
                            }
                            2 -> {
                                val wasliked: Boolean = data_film[pos].likeClicked
                                data_film[pos].likeClicked = !wasliked
                                if (!wasliked) {
                                    //pop up 랜덤 출력
                                    val randomValue = Random.nextInt(2)
                                    val resourceImaga: Int =
                                        if (randomValue == 0) R.drawable.img_pop_up_smile else R.drawable.img_pop_up_heart
                                    val imageView = binding.likePopUpImageView
                                    imageView.setImageResource(resourceImaga)
                                    imageView.visibility = View.VISIBLE
                                    if (!wasliked) {
                                        val scaleUpAnimation = AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.scale_up
                                        )
                                        imageView.startAnimation(scaleUpAnimation)
                                    }
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        imageView.visibility = View.GONE
                                    }, 800)
                                }
                                //버튼 리소스 변경
                                binding.postImageLayoutFilm.likeButton.setBackgroundResource(
                                    if (wasliked)
                                        R.drawable.img_icon_like_unclick
                                    else
                                        R.drawable.img_icon_like_click
                                )
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
                            Log.e("MAIN1HOME_LIKE", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LikeClickResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_LIKE: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
//</editor-fold>
//<editor-fold desc="리사이클러 뷰 접어놓기">
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
                            Log.e("MAIN1HOME_MUSIC", "없는 인덱스 곡입니다. : ${e.message}")
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
            adapter_thema1.musicPlayButtonClickListener = object: AdapterMain1HomeThema1.MusicPlayButtonClickListener {
                override fun OnItemClick(position: Int, isMusicPlaying: Boolean, musicNum: Int) {
                    if (isMusicPlaying) {
                        if(mediaPlayer==null){
                            val musicResourceID = musicArray.getResourceId(musicNum, -1)
                            if (musicResourceID != -1) {
                                mediaPlayer = MediaPlayer.create(requireContext(), musicResourceID)
                                mediaPlayer?.setVolume(1f, 1f)
                            } else {
                                Log.e("MAIN1HOME_MUSIC", "해당하는 음악이 없습니다.")
                            }
                        }
                        playingViewHolderTheme1 = adapter_thema1.currentlyPlayingViewHolder ?: return
                        mediaPlayer?.start()
                        Log.d("MAIN1HOME_MUSIC", "음악 재생!")
                        //배경 색 변경
                        val colorFade = ObjectAnimator.ofObject(
                            viewGroup,
                            "backgroundColor",
                            ArgbEvaluator(),
                            (viewGroup.background as ColorDrawable).color,
                            ContextCompat.getColor(requireContext(), R.color.highlight)
                        )
                        colorFade.duration = 400
                        colorFade.start()
                    } else {
                        mediaPlayer?.pause()
                        Log.d("MAIN1HOME_MUSIC", "음악 일시정지!")
                        //배경 색 변경
                        val colorFade = ObjectAnimator.ofObject(
                            viewGroup,
                            "backgroundColor",
                            ArgbEvaluator(),
                            (viewGroup.background as ColorDrawable).color,
                            ContextCompat.getColor(requireContext(), R.color.white)
                        )
                        colorFade.duration = 400
                        colorFade.start()
                    }
                }
            }
            recyclerView.adapter = adapter_thema1

            //TODO: 나중에 백엔드 연결했을 때 수정해야됨.
            //TODO: 다른 리사이클러뷰(다른 테마)로 변경할 때 데이터가 없어도 VISIBLE로 됨. 이것도 고려해야됨.
            if (data_thema1.isEmpty()) {
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
                        //like button init
                        if (data_thema1[pos].likeClicked) {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                        } else {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                        }
                        //
                        messageTextView.text = data_thema1[pos]?.message
                        if (data_thema1[pos].isFliped) {
                            try {
                                val musicNum = data_thema1[pos].musicNum
                                musicNameTextView.text = musicNameArray[musicNum]
                                singerNameTextView.text = singerArray[musicNum]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                Log.e("MAIN1HOME_MUSIC", "없는 인덱스 곡입니다. : ${e.message}")
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
                        playingViewHolderTheme1 = adapter_thema1.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolderTheme1!!.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager
                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                Log.d("MAIN1HOME_MUSIC", "음악 정지!")
                                mediaPlayer = null
                                playingViewHolderTheme1?.stopMusicAnimation() ?: Log.d("MAIN1HOME_MUSICㅈㄷㄹㅈ","it's null")
                                playingViewHolderTheme1 = null
                                adapter_thema1.currentlyPlayingViewHolder = null
                                //배경 색 변경
                                val colorFade = ObjectAnimator.ofObject(
                                    viewGroup,
                                    "backgroundColor",
                                    ArgbEvaluator(),
                                    (viewGroup.background as ColorDrawable).color,
                                    ContextCompat.getColor(requireContext(), R.color.white)
                                )
                                colorFade.duration = 400
                                colorFade.start()
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
            adapter_thema2 = AdapterMain1HomeThema2(requireContext(), data_thema2)
            adapter_thema2.itemClickListener = object: AdapterMain1HomeThema2.OnItemClickListener {
                override fun OnItemClick(position: Int) {
                    data_thema2[position].isFliped = !data_thema2[position].isFliped
                    if (data_thema2[position].isFliped) {
                        try {
                            val musicNum = data_thema2[position].musicNum
                            musicNameTextView.text = musicNameArray[musicNum]
                            singerNameTextView.text = singerArray[musicNum]
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            Log.e("MAIN1HOME_MUSIC", "없는 인덱스 곡입니다. : ${e.message}")
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
                                Log.e("MAIN1HOME_MUSIC", "해당하는 음악이 없습니다.")
                            }
                        }
                        playingViewHolderTheme2 = adapter_thema2.currentlyPlayingViewHolder ?: return
                        mediaPlayer?.start()
                        Log.d("MAIN1HOME_MUSIC", "음악 재생!")
                        //배경 색 변경
                        val colorFade = ObjectAnimator.ofObject(
                            viewGroup,
                            "backgroundColor",
                            ArgbEvaluator(),
                            (viewGroup.background as ColorDrawable).color,
                            ContextCompat.getColor(requireContext(), R.color.highlight)
                        )
                        colorFade.duration = 400
                        colorFade.start()
                    } else {
                        mediaPlayer?.pause()
                        Log.d("MAIN1HOME_MUSIC", "음악 일시정지!")
                        //배경 색 변경
                        val colorFade = ObjectAnimator.ofObject(
                            viewGroup,
                            "backgroundColor",
                            ArgbEvaluator(),
                            (viewGroup.background as ColorDrawable).color,
                            ContextCompat.getColor(requireContext(), R.color.white)
                        )
                        colorFade.duration = 400
                        colorFade.start()
                    }
                }
            }
            recyclerView.adapter = adapter_thema2

            //TODO: 나중에 백엔드 연결했을 때 수정해야됨.
            //TODO: 다른 리사이클러뷰(다른 테마)로 변경할 때 데이터가 없어도 VISIBLE로 됨. 이것도 고려해야됨.
            if (data_thema2.isEmpty()) {
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
                        //like button init
                        if (data_thema1[pos].likeClicked) {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                        } else {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                        }
                        //
                        messageTextView.text = data_thema2[pos]?.message
                        if (data_thema2[pos].isFliped) {
                            try {
                                val musicNum = data_thema2[pos].musicNum
                                musicNameTextView.text = musicNameArray[musicNum]
                                singerNameTextView.text = singerArray[musicNum]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                Log.e("MAIN1HOME_MUSIC", "없는 인덱스 곡입니다. : ${e.message}")
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
                        playingViewHolderTheme2 = adapter_thema2.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolderTheme2!!.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager
                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                Log.d("MAIN1HOME_MUSIC", "음악 정지!")
                                mediaPlayer = null
                                playingViewHolderTheme2?.stopMusicAnimation()
                                playingViewHolderTheme2 = null
                                adapter_thema2.currentlyPlayingViewHolder = null
                                //배경 색 변경
                                val colorFade = ObjectAnimator.ofObject(
                                    viewGroup,
                                    "backgroundColor",
                                    ArgbEvaluator(),
                                    (viewGroup.background as ColorDrawable).color,
                                    ContextCompat.getColor(requireContext(), R.color.white)
                                )
                                colorFade.duration = 400
                                colorFade.start()
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
            adapter_film = AdapterMain1HomeFilm(requireContext(), data_film)
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
            if (data_film.isEmpty()) {
                viewGroup.visibility = View.INVISIBLE
            }
            //리사이클러뷰 터치 리스너
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
                        //like button init
                        if (data_thema1[pos].likeClicked) {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_click)
                        } else {
                            likeButton.setBackgroundResource(R.drawable.img_icon_like_unclick)
                        }
                        //
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
            currentRequestedTheme = 0
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    dlg.dismiss()
                }
                ThemaNumbering.thema2.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //음악 정지
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    Log.d("Music", "음악 정지!")
                    mediaPlayer = null
                    playingViewHolderTheme2?.stopMusicAnimation()
                    playingViewHolderTheme2 = null
                    adapter_thema2.currentlyPlayingViewHolder = null
                    //배경 색 변경
                    binding.postImageLayoutThema2.viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                    binding.postImageLayoutThema2.viewGroup.visibility = View.INVISIBLE
                    if (data_thema1.isNotEmpty()) {
                        binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.thema1.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }

                ThemaNumbering.film.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //
                    binding.postImageLayoutFilm.viewGroup.visibility = View.INVISIBLE
                    if (data_thema1.isNotEmpty()) {
                        binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.thema1.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }
            }
        }
        //Dialog에서 thema2을 선택했을 경우
        bindingDialog.postFrame2.setOnClickListener {
            currentRequestedTheme = 1
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //음악 정지
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    Log.d("Music", "음악 정지!")
                    mediaPlayer = null
                    playingViewHolderTheme1?.stopMusicAnimation() ?: Log.d("ee","it's null")
                    playingViewHolderTheme1 = null
                    adapter_thema1.currentlyPlayingViewHolder = null
                    //배경 색 변경
                    binding.postImageLayoutThema1.viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                    binding.postImageLayoutThema1.viewGroup.visibility = View.INVISIBLE
                    if (data_thema2.isNotEmpty()) {
                        binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.thema2.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }

                ThemaNumbering.thema2.value -> {
                    dlg.dismiss()
                }

                ThemaNumbering.film.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //
                    binding.postImageLayoutFilm.viewGroup.visibility = View.INVISIBLE
                    if (data_thema2.isNotEmpty()) {
                        binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.thema2.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }
            }
        }
        //Dialog에서 Film을 선택했을 경우
        bindingDialog.postFrameFilm.setOnClickListener {
            currentRequestedTheme = 2
            when (current_selected_thema) {
                ThemaNumbering.thema1.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //음악 정지
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    Log.d("Music", "음악 정지!")
                    mediaPlayer = null
                    playingViewHolderTheme1?.stopMusicAnimation()
                    playingViewHolderTheme1 = null
                    adapter_thema1.currentlyPlayingViewHolder = null
                    //배경 색 변경
                    binding.postImageLayoutThema1.viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                    binding.postImageLayoutThema1.viewGroup.visibility = View.INVISIBLE
                    if (data_thema2.isNotEmpty()) {
                        binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.film.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }

                ThemaNumbering.thema2.value -> {
                    //글 로드 중지
                    handler.removeCallbacks(loadMainRunnable)
                    //음악 정지
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    Log.d("Music", "음악 정지!")
                    mediaPlayer = null
                    playingViewHolderTheme2?.stopMusicAnimation()
                    playingViewHolderTheme2 = null
                    adapter_thema2.currentlyPlayingViewHolder = null
                    //배경 색 변경
                    binding.postImageLayoutThema2.viewGroup.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                    binding.postImageLayoutThema2.viewGroup.visibility = View.INVISIBLE
                    if (data_thema2.isNotEmpty()) {
                        binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
                    }
                    current_selected_thema = ThemaNumbering.film.value
                    dlg.dismiss()
                    //글 로드 재개
                    loadPosts(currentRequestedTheme)
                }

                ThemaNumbering.film.value -> {
                    dlg.dismiss()
                }
            }
        }
    }
//</editor-fold>

//<editor-fold desc="댓글 부분 접어놓기">
    //댓글 Dialog
    private fun showCommentDialog() {
        //dialog 객체 생성
        val builder = AlertDialog.Builder(requireContext())
        val dlg = builder.setView(commentDialogBinding.root).show()
        dlg.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dlg.window?.setGravity(Gravity.BOTTOM)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog 사라질 때 콜백 함수
        dlg.setOnDismissListener {
            commentDialogBinding.apply {
                textTextView.text = ""
                userIdStringTextView.text = ""
                dateTextView.text = ""
            }
        }
    }
    private fun initCommentRecyclerView() {
        commentDialogBinding.apply {
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
    private fun startProfileActivity(id: Long) {
        val i = Intent(requireContext(), ProfileActivity::class.java)
        i.putExtra("requesterId",this.id)
        i.putExtra("targetId",id)
        startActivity(i)
    }
    //댓글 창 위쪽 데이터 설정
    private fun setPostDataInCommentWindow(position: Int) {
        commentDialogBinding.apply {
            when (currentRequestedTheme) {
                0 -> {
                    var idSum: String = ""
                    data_thema1[position].participantUserIds.forEach { id -> idSum = idSum + ", " + id.toString() }
                    userIdStringTextView.text = idSum
                    dateTextView.text = data_thema1[position].date.toString()
                    textTextView.text = data_thema1[position].message
                }
                1 -> {
                    var idSum: String = ""
                    data_thema2[position].participantUserIds.forEach { id -> idSum = idSum + ", " + id.toString() }
                    userIdStringTextView.text = idSum
                    dateTextView.text = data_thema2[position].date.toString()
                    textTextView.text = data_thema2[position].message
                }
                2 -> {
                    var idSum: String = ""
                    data_film[position].participantUserIds.forEach { id -> idSum = idSum + ", " + id.toString() }
                    userIdStringTextView.text = idSum
                    dateTextView.text = data_film[position].date.toString()
                    textTextView.text = data_film[position].message
                }
            }
        }
    }
    //네에에에트으으으으으워어어어어크으으으으으으
    private fun loadComments(postId: Long, position: Int) {
        loadCommentsRequest(postId, position)
    }
    private fun loadCommentsRequest(postId: Long, position: Int) {
        val call = RetrofitBuilder.api.loadComments(postId)
        call.enqueue(object : Callback<List<CommentResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<CommentResponse>>,
                response: Response<List<CommentResponse>>
            ) {
                Log.e("MAIN1HOME_COMMENT1", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val commentsList  = response.body()
                    if (commentsList == null) {
                        Log.e("MAIN1HOME_COMMENT2", "응답이 null입니다.")
                        return
                    } else if (commentsList.count() == 0) {
                        Log.e("MAIN1HOME_COMMENT3", "응답이 null입니다.")
                        return
                    } else {
                        Log.e("MAIN1HOME_COMMENT4", "댓글 로드 성공!")
                        commentDialogBinding.userIdStringTextView
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
                            Log.e("MAIN1HOME_COMMENT5", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_COMMENT6: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //댓글 추가
    private fun createComment(postId: Long, userId: Long, commentContent: String) {
        val request = CreateComment(postId, userId, commentContent)
        createCommentRequest(request, postId)
    }
    private fun createCommentRequest(request: CreateComment, postId: Long) {
        val call = RetrofitBuilder.api.createComment(request)
        call.enqueue(object : Callback<CommentResponse> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                Log.e("MAIN1HOME_CREATE_COMMENT1", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val commentResponse  = response.body()
                    if (commentResponse == null) {
                        Log.e("MAIN1HOME_CREATE_COMMENT2", "응답이 null입니다.")
                        return
                    } else  {
                        when (currentRequestedTheme) {
                            0 -> {
                                val layoutManager = binding.postImageLayoutThema1.recyclerView.layoutManager as LinearLayoutManager
                                val position = layoutManager.findFirstVisibleItemPosition()
                                loadComments(postId, position)
                            }
                            1 -> {
                                val layoutManager = binding.postImageLayoutThema2.recyclerView.layoutManager as LinearLayoutManager
                                val position = layoutManager.findFirstVisibleItemPosition()
                                loadComments(postId, position)
                            }
                            2 -> {
                                val layoutManager = binding.postImageLayoutFilm.recyclerView.layoutManager as LinearLayoutManager
                                val position = layoutManager.findFirstVisibleItemPosition()
                                loadComments(postId, position)
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
                            Log.e("MAIN1HOME_CREATE_COMMENT3", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE_CREATE_COMMENT4: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
//</editor-fold>
    private fun loadPosts(themaNum: Int) {
        //요청을 보낼 시간 설정
        val lastPostDate: LocalDateTime? = when(themaNum) {
            0 -> {
                if (data_thema1.isEmpty())
                    LocalDateTime.now()
                else
                    data_thema1[data_thema1.count() - 1].date
            }
            1 -> {
                if (data_thema2.isEmpty())
                    LocalDateTime.now()
                else
                    data_thema2[data_thema2.count() - 1].date
            }
            2 -> {
                if (data_film.isEmpty())
                    LocalDateTime.now()
                else
                    data_film[data_film.count() - 1].date
            }
            else -> {
                LocalDateTime.now()
            }
        }
        when(themaNum) {
            0 -> {
                if (data_thema1.isEmpty())
                    binding.postImageLayoutThema1.viewGroup.visibility = View.INVISIBLE
            }
            1 -> {
                if (data_thema2.isEmpty())
                    binding.postImageLayoutThema2.viewGroup.visibility = View.INVISIBLE
            }
            2 -> {
                if (data_film.isEmpty())
                    binding.postImageLayoutFilm.viewGroup.visibility = View.INVISIBLE
            }
        }
        loadPostRequest(this.id, themaNum, lastPostDate.toString())
    }
    fun loadPostRequest(id: Long, theme: Int, lastPostDate: String){
        val call = RetrofitBuilder.api.main1LoadPostRequest(id,theme,lastPostDate)
        call.enqueue(object : Callback<List<Main1LoadPostResponse>> { // 비동기 방식 통신 메소드
            override fun onResponse(
                call: Call<List<Main1LoadPostResponse>>,
                response: Response<List<Main1LoadPostResponse>>
            ) {
                Log.e("MAIN1HOME", response.raw().request.url.toString())
                if(response.isSuccessful()){ // 응답 잘 받은 경우
                    val postList  = response.body()
                    if (postList == null) {
                        Log.e("MAIN1HOME", "응답이 null입니다.")

                        reload()
                        return
                    }

                    Log.d("MAIN1HOME", "응답 성공 / post 개수 : " + postList.size.toString())
                    //데이터 처리
                    val beforeDataCount = when (theme) {
                        0 -> data_thema1.count()
                        1 -> data_thema2.count()
                        2 -> data_film.count()
                        else -> 0
                    }
                    postList?.forEach { post ->
                        val newPost = Post()
                        newPost.imgArray[0] = baseUrl + post.images[0]
                        newPost.imgArray[1] = baseUrl + post.images[1]
                        newPost.imgArray[2] = baseUrl + post.images[2]
                        newPost.imgArray[3] = baseUrl + post.images[3]
                        newPost.theme = post.theme
                        //TODO: postId랑 userIds랑 구별해야됨.
                        newPost.postId = post.postId
                        newPost.likeCount = post.likeCount
                        val dateString = post.date
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                        newPost.date = LocalDateTime.parse(dateString, formatter)
                        Log.d("MAIN1HOME", newPost.date.toString())
                        newPost.message = post.text
                        newPost.musicNum = post.musicNum
                        newPost.participantUserIds = post.participantUserIds
                        newPost.likeClicked = post.isLikeClicked

                        when(theme) {
                            0 -> data_thema1.add(newPost)
                            1 -> data_thema2.add(newPost)
                            2 -> data_film.add(newPost)
                        }
                    }
                    when(theme) {
                        0 -> {
                            if (beforeDataCount == data_thema1.count()) {
                                reload()
                            } else {
                                Log.d("MAIN1HOME", "theme1 게시물이 로드되었습니다. data_set개수 : " + data_thema1.count())
                                adapter_thema1.notifyDataSetChanged()
                                binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                            }
                        }
                        1 -> {
                            if (beforeDataCount == data_thema2.count()) {
                                reload()
                            } else {
                                Log.d("MAIN1HOME","theme2 게시물이 로드되었습니다. 현재 data_set개수 : " + data_thema2.count() + " / 이전 data_set개수 : " + beforeDataCount.toString())
                                adapter_thema2.notifyDataSetChanged()
                                binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                            }
                        }
                        2 -> {
                            if (beforeDataCount == data_film.count()) {
                                reload()
                            } else {Log.d("MAIN1HOME","theme_film 게시물이 로드되었습니다. data_set개수 : " + data_film.count())
                                adapter_film.notifyDataSetChanged()
                                binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
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
                            reload()
                        } catch (e: JSONException) {
                            Log.e("MAIN1HOME", "Failed to parse error response: $errorBody")
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            reload()
                        }
                    } else {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        reload()
                    }
                }
            }
            override fun onFailure(call: Call<List<Main1LoadPostResponse>>, t: Throwable) {
                // 통신에 실패한 경우
                Log.d("MAIN1HOME CONNECTION FAILURE: ", t.localizedMessage)
                Toast.makeText(requireContext(), "서버와의 통신에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
                reload()
            }
        })
    }
    fun reload() {
        handler.postDelayed(loadMainRunnable, 5000)
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