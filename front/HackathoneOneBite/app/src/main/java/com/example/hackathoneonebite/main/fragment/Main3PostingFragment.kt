package com.example.hackathoneonebite.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hackathoneonebite.R

class Main3PostingFragment : Fragment() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val circleViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main3_posting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postImageLayoutFilm = view.findViewById<View>(R.id.filmImage)
        val postImageLayout1 = view.findViewById<View>(R.id.thema1Image)
        val postImageLayout2 = view.findViewById<View>(R.id.thema2Image)

        val postImageLayouts = arrayOf(postImageLayoutFilm, postImageLayout1, postImageLayout2)

        val film = view.findViewById<View>(R.id.circleFilm)
        val theme1 = view.findViewById<View>(R.id.circleTheme1)
        val theme2 = view.findViewById<View>(R.id.circleTheme2)


        for ((index, layout) in postImageLayouts.withIndex()) {
            layout.setOnClickListener {
                Log.d("index", index.toString())
                Log.d("layout",layout.toString())

                when(index){
                    0 -> film.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                    1 -> theme1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                    2 -> theme2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight))

                }

                mainHandler.postDelayed({
                    val intent = Intent(requireContext(), Main3PostingMakingActivity::class.java)
                    intent.putExtra("layout_id", index)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    film.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                    theme1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                    theme2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                }, 100)

            }
        }
    }
}