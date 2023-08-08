package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
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

    enum class ThemaNumbering(val value: Int) {
        thema1(0),
        thema2(1),
        film(2)
    }

    var current_selected_thema: Int = ThemaNumbering.thema1.value

    override fun onAttach(context: Context) {
        Log.d("onAttach", "onAttach called");
        super.onAttach(context)
        initData(data_thema1)
        initData(data_thema2)
        initData(data_film)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("onCreateView", "onCreateView called");
        binding = FragmentMain1HomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("onViewCreated", "onViewCreated called");
        super.onViewCreated(view, savedInstanceState)

        initSelectedThema()
        initRecyclerView()
        init()
    }

    override fun onStart() {
        Log.d("onStart", "onStart called");
        super.onStart()
    }

    override fun onResume() {
        Log.d("onResume", "onResume called");
        super.onResume()
    }

    private fun initData(data: ArrayList<Post>) { //백엔드와 연결 전 단계에 테스트를 위해 데이터 생성하는 함수
        for(i in 0..100) {
            data.add(Post())
            data[i].id = "kakaka"
            data[i].likeCount = 10
            data[i].date = LocalDateTime.now()
            data[i].message = i.toString() + i.toString() + i.toString() + i.toString() + i.toString() + i.toString() + i.toString() + i.toString() + i.toString()
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

    private fun initRecyclerView() { //리사이클러뷰 초기화
        //Thema1 RecyclerView
        binding.postImageLayoutThema1.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter_thema1 = AdapterMain1HomeThema1(data_thema1)
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
        //Film RecyclerView
        binding.postImageLayoutFilm.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter_film = AdapterMain1HomeFilm(data_film)
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

            /*//item간에 간격 조정
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val itemHeight = recyclerView.height
                    val itemDecoration = CenterItemDecoration(itemHeight)
                    recyclerView.addItemDecoration(itemDecoration)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        recyclerView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                }
            })
            //----*/
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
                }
            })
        }
    }
    //스크롤 밝기 조절을 위한 함수
    private fun adjustBrightness(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
/*
        //TODO: 이 경우(새로 나오는 것만 밝기가 바뀌는 경우) adapter에서 alpha값을 1f로 초기화 시켜줘야 됨.
        val itemView = layoutManager.findViewByPosition(lastVisibleItemPosition)
        val distance = Math.abs(itemView!!.bottom - recyclerView.height)
        val maxDistance = recyclerView.height
        val alpha = 1 - (distance * 1f / maxDistance)
        Log.d("정보", "distance:"+distance.toString()+"/alpha:"+alpha.toString())
        val filmLayout:ConstraintLayout = itemView.findViewById(R.id.postImageLayout)
        filmLayout.findViewById<ImageView>(R.id.imageView1).alpha = alpha
        filmLayout.findViewById<ImageView>(R.id.imageView2).alpha = alpha
        filmLayout.findViewById<ImageView>(R.id.imageView3).alpha = alpha
        filmLayout.findViewById<ImageView>(R.id.imageView4).alpha = alpha*/

        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val itemView = layoutManager.findViewByPosition(i)

            // 알파 값 계산
            val distance = Math.abs(itemView!!.bottom - recyclerView.height)
            val maxDistance = recyclerView.height * 0.6f
            val alpha: Float
            if(distance > maxDistance)
                alpha = 0f
            else
                alpha = 1 - ((distance - 0.4f) / maxDistance)

            val filmLayout:ConstraintLayout = itemView.findViewById(R.id.postImageLayout)
            filmLayout.findViewById<ImageView>(R.id.imageView1).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView2).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView3).alpha = alpha
            filmLayout.findViewById<ImageView>(R.id.imageView4).alpha = alpha
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

class CenterItemDecoration(private val itemHeight: Int) : RecyclerView.ItemDecoration() { //리사이클러뷰의 좌우 간격 설정을 위한 클래스
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
}
