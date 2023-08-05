package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hackathoneonebite.R

class Main3PostingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main3_posting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postImageLayout_pseudo = view.findViewById<View>(R.id.postImageLayout_pseudo)
        val postImageLayout1 = view.findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = view.findViewById<View>(R.id.postImageLayout2)


        val postImageLayouts = arrayOf(postImageLayout_pseudo, postImageLayout1, postImageLayout2)

        for ((index, layout) in postImageLayouts.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(requireContext(), Main3PostingMakingActivity::class.java)
                intent.putExtra("layout_id", index)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }


    }
}