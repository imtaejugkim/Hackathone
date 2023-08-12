package com.example.hackathoneonebite.main.fragment

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingTimeBinding

class Main3PostingTimeActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private var initialY: Float = 0f
    private var currentValue: Int = 0
    lateinit var binding : ActivityMain3PostingTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        textView = binding.textView
        textView.text = formatTime(currentValue)

        val selectedName = intent.getStringExtra("selected_name")
        binding.selectName.text = selectedName
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        textView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialY = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaY = event.y - initialY
                        val newValue = currentValue - (deltaY / textView.height * 1440).toInt() // 1440 minutes in a day

                        if (newValue in 0..1439) {
                            currentValue = newValue
                            textView.text = formatTime(currentValue)
                        }

                        initialY = event.y
                    }
                }
                return true
            }
        })

        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val rightArrow = binding.rightArrow
        rightArrow.setOnClickListener {
            val nextIntent = Intent(this, Main3PostingRequestActivity::class.java)
            nextIntent.putExtra("selected_name", selectedName)
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }





    private fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format("%02d:%02d", hours, mins)
    }
}


