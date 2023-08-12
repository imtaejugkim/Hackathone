package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain4RankingTopBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding
import java.time.LocalDateTime

class Main4RankingFragment : Fragment() {
    var fragment_width: Int = 0
    var fragment_height: Int = 0
    var profileBackgroundImageHeight: Int = 0
    val profileInfoViewHeight: Int = 200
    val profileInfoViewHeightWhenScollDown: Int = 180
    var transientThreshold: Int = 0 //배경이 아예 사라지는 임계 위치
    val autoDragRatioThanProfileBackgroundHeight: Float = 0.6f //프로필 배경의 얼마만큼의 비율만큼 움직여야 배경을 사라지게 할 것인가
    /*//data
    val data_thema1: ArrayList<Post> = ArrayList()
    val data_thema2: ArrayList<Post> = ArrayList()
    val data_film: ArrayList<Post> = ArrayList()
    lateinit var musicArray: TypedArray*/
    lateinit var binding: FragmentMain4RankingTopBinding
    private lateinit var adapter: AdapterMain4RankingFragment
    override fun onAttach(context: Context) {
        super.onAttach(context)

        /*musicArray = resources.obtainTypedArray(R.array.music_array)
        initData(data_thema1)
        initData(data_thema2)
        initData(data_film)*/
    }
    /*private fun initData(data: ArrayList<Post>) { //백엔드와 연결 전 단계에 테스트를 위해 데이터 생성하는 함수
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
    }*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain4RankingTopBinding.inflate(layoutInflater, container, false)
        adapter = AdapterMain4RankingFragment()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            fragment_width = view.width
            fragment_height = view.height
            profileBackgroundImageHeight = fragment_height - dpToPx(profileInfoViewHeight)
            transientThreshold = (profileBackgroundImageHeight * autoDragRatioThanProfileBackgroundHeight).toInt()
            initLayout()
            initScrollview()
            initRecyclerview()

            Log.d("fragment height", fragment_height.toString())
        }
    }

    private fun initRecyclerview() {
        val nameList = mutableListOf<String>()

        for (i in 1..100) {
            nameList.add("Name $i")
        }
        adapter.setData(nameList)
    }

    private fun initScrollview() {
        binding.scrollView.apply {
            binding.scrollView.setOnScrollChangeListener { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                val alpha: Float =
                    if (scrollY > transientThreshold) 0f
                    else 1 - (scrollY - (1 - autoDragRatioThanProfileBackgroundHeight)) / (transientThreshold)
                binding.rankingMain.alpha = alpha
                binding.recyclerView.alpha = alpha
            }
        }
    }

    private fun initLayout() {
        val paramsProfile = binding.rankingTopView.layoutParams
        paramsProfile.height = fragment_height
        binding.rankingTopView.layoutParams = paramsProfile

        val paramsProfileBottom = binding.rankingBottomLayout.viewGroupRankingBottom.layoutParams
        paramsProfileBottom.height = fragment_height - dpToPx(profileInfoViewHeightWhenScollDown)
        binding.rankingBottomLayout.viewGroupRankingBottom.layoutParams = paramsProfileBottom
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}