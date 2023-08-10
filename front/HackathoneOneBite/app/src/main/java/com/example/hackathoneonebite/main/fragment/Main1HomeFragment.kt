package com.example.hackathoneonebite.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
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
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.DialogMain1TopBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import java.time.LocalDateTime


class Main1HomeFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeBinding
    lateinit private var adapter_thema1: AdapterMain1HomeThema1
    lateinit private var adapter_thema2: AdapterMain1HomeThema2
    lateinit private var adapter_film: AdapterMain1HomeFilm
    val data_thema1: ArrayList<Post> = ArrayList()
    val data_thema2: ArrayList<Post> = ArrayList()
    val data_film: ArrayList<Post> = ArrayList()
    var fragment_width: Int = 0
    var fragment_height: Int = 0
    lateinit var musicArray: TypedArray

    enum class ThemaNumbering(val value: Int) {
        thema1(0),
        thema2(1),
        film(2)
    }

    var current_selected_thema: Int = ThemaNumbering.thema1.value

    override fun onAttach(context: Context) {
        //Log.d("onAttach", "onAttach called");
        super.onAttach(context)
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
        initSelectedThema()
        initRecyclerView()
        init()
    }

    private fun initData(data: ArrayList<Post>) { //백엔드와 연결 전 단계에 테스트를 위해 데이터 생성하는 함수
        for(i in 0..100) {
            data.add(Post())
            data[i].id = 123123
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
                0 -> binding.postImageLayoutThema1.viewGroup.visibility = View.VISIBLE
                1 -> binding.postImageLayoutThema2.viewGroup.visibility = View.VISIBLE
                2 -> binding.postImageLayoutFilm.viewGroup.visibility = View.VISIBLE
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

                        //music play
                        //centerView.findViewById<ConstraintLayout>(R.id.postImageLayoutBack).findViewById<Button>(R.id.playButton).performClick()

                        //music stop
                        val playingViewHolder = adapter_thema1.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolder.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager

                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
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

                        //music stop
                        val playingViewHolder = adapter_thema2.currentlyPlayingViewHolder ?: return
                        val position = playingViewHolder.adapterPosition
                        val layoutManager2 = recyclerView.layoutManager

                        if (!layoutManager2!!.isViewCompletelyVisible(position)) {
                            if (position != pos) {
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
