package com.example.hackathoneonebite.main.fragment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain2SearchBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding

class Main2SearchFragment : Fragment() {

    lateinit var binding : FragmentMain2SearchBinding
    private lateinit var nameAdapter: AdapterMain2SearchFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain2SearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameAdapter = AdapterMain2SearchFragment()
        binding.nameRecyclerView.adapter = nameAdapter
        binding.nameRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.beforeSearch.visibility = View.VISIBLE
                    binding.nameRecyclerView.visibility = View.GONE
                    nameAdapter.clearData() // 텍스트가 비어있을 경우 RecyclerView에 표시되는 이름을 모두 지움

                } else {
                    binding.beforeSearch.visibility = View.GONE
                    binding.nameRecyclerView.visibility = View.VISIBLE
                    // TODO: 데베 데이터를 가져와서 여기서 나오게 해야함
                    val nameList = mutableListOf<String>()

                    for (i in 1..100) {
                        nameList.add("Name $i")
                    }
                    nameAdapter.setData(nameList)
                    nameAdapter.filter.filter(s)
                    updateClearButtonVisibility(true) // 이미지를 변경하여 보임
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEdit.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val isClearButtonClicked = event.rawX >= (binding.searchEdit.right - binding.searchEdit.compoundDrawables[2].bounds.width())
                if (isClearButtonClicked) {
                    binding.searchEdit.text.clear()
                    updateClearButtonVisibility(false)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // 초기에는 clear 버튼이 보이지 않도록 초기화
        updateClearButtonVisibility(false)
    }

    private fun updateClearButtonVisibility(visible: Boolean) {
        val drawable = if (visible) R.drawable.baseline_clear_24 else R.drawable.baseline_search_24
        binding.searchEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, requireContext().getDrawable(drawable), null
        )
    }

    private fun showThemeFilmRecyclerView() {
        binding.themeFilmRecyclerView.visibility = View.VISIBLE
        binding.theme1RecyclerView.visibility = View.GONE
        binding.theme2RecyclerView.visibility = View.GONE
        binding.nameRecyclerView.visibility = View.GONE
        binding.beforeSearch.visibility = View.GONE
    }

    private fun showTheme1RecyclerView() {
        binding.themeFilmRecyclerView.visibility = View.GONE
        binding.theme1RecyclerView.visibility = View.VISIBLE
        binding.theme2RecyclerView.visibility = View.GONE
        binding.nameRecyclerView.visibility = View.GONE
        binding.beforeSearch.visibility = View.GONE
    }

    private fun showTheme2RecyclerView() {
        binding.themeFilmRecyclerView.visibility = View.GONE
        binding.theme1RecyclerView.visibility = View.GONE
        binding.theme2RecyclerView.visibility = View.VISIBLE
        binding.nameRecyclerView.visibility = View.GONE
        binding.beforeSearch.visibility = View.GONE
    }



}