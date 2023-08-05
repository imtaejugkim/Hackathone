package com.example.hackathoneonebite.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide.init
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeFirstBinding

class Main1HomeFirstFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain1HomeFirstBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.thema1Image.setOnClickListener {

        }
        binding.thema2Image.setOnClickListener {

        }
        binding.filmImage.setOnClickListener {

        }
    }
}