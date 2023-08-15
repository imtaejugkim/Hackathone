package com.example.hackathoneonebite.main.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.MyApplication.Companion.imageByteArrays
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingTimeBinding
import okhttp3.MultipartBody

class Main3PostingTimeActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private var initialY: Float = 0f
    private var currentValue: Int = 0
    lateinit var binding : ActivityMain3PostingTimeBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textView = binding.textView
        textView.text = formatTime(currentValue)

        val receivedIntent = intent
        val receivedPost = receivedIntent.getSerializableExtra("post_data") as? Post
        val selectedName = intent.getStringExtra("selected_name")
        binding.selectName.text = selectedName

        val imgPartArray = Array(4) { 0 }

        val sensitivityFactor = 0.001 // 조절할 민감도 계수
        var lastEventTime = 0L

        textView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialY = event.y
                        lastEventTime = System.currentTimeMillis()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val currentTime = System.currentTimeMillis()
                        val deltaTime = currentTime - lastEventTime
                        if (deltaTime > 0) {
                            val deltaY = event.y - initialY
                            val speed = deltaY / deltaTime
                            val newValue = currentValue - (speed * 1440 * sensitivityFactor).toInt()

                            if (newValue in 0..1439) {
                                currentValue = newValue
                                textView.text = formatTime(currentValue)
                            }

                            initialY = event.y
                            lastEventTime = currentTime
                        }
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
            nextIntent.putExtra("selected_time", currentValue) // 시간 값을 전달
            nextIntent.putExtra("post_data", receivedPost)
            nextIntent.putExtra("imagePartSize", imgPartArray.size)

            for (i in 0 until imageByteArrays.size) {
                nextIntent.putExtra("imageByteArrays$i", imageByteArrays[i]) // Pass individual byte arrays to the next screen
                Log.d("new 보냄 ", imageByteArrays[i].toString())
            }

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


