package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.FragmentMain1HomeBinding
import com.example.hackathoneonebite.databinding.FragmentMain1HomeFirstBinding
import com.example.hackathoneonebite.main.ViewPageAdapter

class Main1HomeFirstFragment : Fragment() {
    lateinit var binding: FragmentMain1HomeFirstBinding
    private var buttonClickListener: ViewPageAdapter.OnFragmentButtonClickListener? = null
    //var viewPager: ViewPager2? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewPageAdapter.OnFragmentButtonClickListener) {
            buttonClickListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentButtonClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMain1HomeFirstBinding.inflate(layoutInflater, container, false)
        //viewPager = activity?.findViewById(R.id.main_viewPager)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDetach() {
        super.onDetach()
        buttonClickListener = null
    }

    private fun init() {
        binding.thema1Image.setOnClickListener {
            buttonClickListener?.onButtonClick(0, Main1HomeFragment(), 0)
        }
        binding.thema2Image.setOnClickListener {
            buttonClickListener?.onButtonClick(0, Main1HomeFragment(), 1)
        }
        binding.filmImage.setOnClickListener {
            buttonClickListener?.onButtonClick(0, Main1HomeFragment(), 2)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.viewGroupForSelect, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}