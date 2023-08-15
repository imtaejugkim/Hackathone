package com.example.hackathoneonebite.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain2SearchBinding
import com.example.hackathoneonebite.databinding.FragmentMain5ProfileBinding

class Main2SearchFragment : Fragment() {

    lateinit var binding : FragmentMain2SearchBinding
    private lateinit var nameAdapter: AdapterMain2SearchFragment
    private var isInitialStateVisible = true


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

        nameAdapter.setOnNameClickListener(object : AdapterMain2SearchFragment.OnNameClickListener {
            override fun onNameClick(name: String) {
                val fragment = Main5ProfileFragment()
                val transaction = parentFragmentManager.beginTransaction()

                parentFragmentManager.fragments.forEach { existingFragment ->
                    transaction.hide(existingFragment)
                }
                val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)


                binding.afterTransaction.visibility = View.VISIBLE
                transaction.add(R.id.afterTransaction, fragment)
                transaction.addToBackStack(null)
                binding.beforeTransaction.visibility = View.GONE

                transaction.commit()
            }
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

        val postImageLayoutFilm = binding.filmImage
        val postImageLayout1 = binding.filmTheme1
        val postImageLayout2 = binding.filmTheme2
        val postImageLayouts = arrayOf(postImageLayoutFilm, postImageLayout1, postImageLayout2)

        for ((index, layout) in postImageLayouts.withIndex()) {
            layout.setOnClickListener {
                when(index){
                    0 -> {
                        binding.circleFilm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                        binding.circleTheme1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        binding.circleTheme2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        showThemeFilmRecyclerView()

                    }
                    1 -> {
                        binding.circleFilm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        binding.circleTheme1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                        binding.circleTheme2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        showTheme1RecyclerView()
                    }
                    2 -> {
                        binding.circleFilm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        binding.circleTheme1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                        binding.circleTheme2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                        showTheme2RecyclerView()
                    }
                }
            }
        }

        // 초기에는 clear 버튼이 보이지 않도록 초기화
        updateClearButtonVisibility(false)
    }


    private fun restoreLayoutState() {
        if (isInitialStateVisible) {
            binding.beforeTransaction.visibility = View.VISIBLE
            binding.afterTransaction.visibility = View.GONE
        } else {
            binding.beforeTransaction.visibility = View.GONE
            binding.afterTransaction.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isInitialStateVisible", isInitialStateVisible)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            isInitialStateVisible = savedInstanceState.getBoolean("isInitialStateVisible", true)
            restoreLayoutState()
        }
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

        val themeFilmAdapter = AdapterMain2SearchThemeFilm()
        binding.theme1RecyclerView.adapter = themeFilmAdapter
        binding.theme1RecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun showTheme1RecyclerView() {
            binding.theme1RecyclerView.visibility = View.VISIBLE
            binding.theme2RecyclerView.visibility = View.GONE
            binding.themeFilmRecyclerView.visibility = View.GONE
            binding.nameRecyclerView.visibility = View.GONE

            val theme1Adapter = AdapterMain2SearchTheme1()
            binding.theme1RecyclerView.adapter = theme1Adapter
            binding.theme1RecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    private fun showTheme2RecyclerView() {
            binding.theme2RecyclerView.visibility = View.VISIBLE
            binding.theme1RecyclerView.visibility = View.GONE
            binding.themeFilmRecyclerView.visibility = View.GONE
            binding.nameRecyclerView.visibility = View.GONE

            val theme2Adapter = AdapterMain2SearchTheme2()
            binding.theme2RecyclerView.adapter = theme2Adapter
            binding.theme2RecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }



}