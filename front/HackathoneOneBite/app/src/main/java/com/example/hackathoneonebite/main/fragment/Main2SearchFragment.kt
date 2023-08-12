package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import com.example.hackathoneonebite.databinding.FragmentMain2SearchBinding

class Main2SearchFragment : Fragment() {

    lateinit var binding : FragmentMain2SearchBinding
    private lateinit var adapter: AdapterMain2SearchFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain2SearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterMain2SearchFragment()

        binding.nameRecyclerView.adapter = adapter
        binding.nameRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        // 검색 레이아웃 외부를 클릭했을 때 검색 레이아웃을 숨김
        binding.root.setOnClickListener {
            if (binding.nameRecyclerView.visibility == View.VISIBLE) {
                binding.nameRecyclerView.visibility = View.GONE
                binding.beforeSearch.visibility = View.VISIBLE
            }
        }

        binding.searchEdit.setOnClickListener {
            if (binding.beforeSearch.visibility == View.VISIBLE) {
                binding.nameRecyclerView.visibility = View.VISIBLE
                binding.beforeSearch.visibility = View.GONE
            }
            adapter.setOnNameClickListener(object : AdapterMain2SearchFragment.OnNameClickListener {
                override fun onNameClick(name: String) {

                }
            })

            binding.searchEdit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    adapter.clearData() // 포커스가 없으면 RecyclerView에 표시되는 이름을 모두 지움
                }
            }


            binding.searchEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        adapter.clearData() // 텍스트가 비어있을 경우 RecyclerView에 표시되는 이름을 모두 지움
                    } else {
                        // TODO: 데베 데이터를 가져와서 여기서 나오게 해야함
                        val nameList = mutableListOf<String>()

                        for (i in 1..100) {
                            nameList.add("Name $i")
                        }
                        adapter.setData(nameList)
                        adapter.filter.filter(s)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    }



}