package com.example.hackathoneonebite.main.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import com.mig35.carousellayoutmanager.CarouselLayoutManager
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.mig35.carousellayoutmanager.CenterScrollListener
import java.time.LocalDate


class Main1HomeFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeBinding
    lateinit private var adapter: AdapterMain1Home
    val data: ArrayList<Post> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain1HomeBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }



    private fun initData() {
        for(i in 0..100) {
            data.add(Post())
            data[i].id = "kakaka"
            data[i].likeCount = 10
            data[i].date = LocalDate.of(2023, 1, 3)
            data[i].message = "하하하하하하하하하"

            if(i % 3 == 1) {
                data[i].frame = "frame2"
            } else {
                data[i].frame = "frame1"
            }

            for (j in 0..3) {
                if(j == i % 4) {
                    data[i].imgArray[j] = R.drawable.test_image1
                } else {
                    data[i].imgArray[j] = R.drawable.test_image2
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            recyclerView.setHasFixedSize(true)
            val carouselLayoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL)
            carouselLayoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
            recyclerView.layoutManager = carouselLayoutManager
            recyclerView.addOnScrollListener(CenterScrollListener())
            adapter = AdapterMain1Home(data)
            recyclerView.adapter = adapter

            /*recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            //recyclerView.layoutManager = Main1homeCircleLayoutManager(requireContext())
            adapter = AdapterMain1Home(data)
            recyclerView.adapter = adapter

            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val itemWidth = (recyclerView.height * 4.0 / 10).toInt()
                    val itemDecoration = CenterItemDecoration(itemWidth)
                    recyclerView.addItemDecoration(itemDecoration)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        recyclerView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                }
            })

            // PagerSnapHelper를 사용하여 아이템을 중앙에 스냅시킴
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)*/
        }
    }
}

class CenterItemDecoration(private val itemWidth: Int) : RecyclerView.ItemDecoration() {
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
        outRect.left = (totalWidth - itemWidth) / 2
        outRect.right = (totalWidth - itemWidth) / 6
    }

    private fun ensureItemPosition(outRect: Rect, parent: RecyclerView) {
        val totalWidth = parent.width
        outRect.left = (totalWidth - itemWidth) / 6
        outRect.right = (totalWidth - itemWidth) / 6
    }
}