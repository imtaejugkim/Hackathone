package com.example.hackathoneonebite.main.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.databinding.ActivityMain3PostingNowUploadBinding

class Main3PostingNowUploadActivity : AppCompatActivity(),
    AdapterMain3PostingUpload.OnButtonClickListener {

    private var isRotating = false
    private var isPlaying = false
    private var rotationAnimator: ValueAnimator? = null
    private var mediaPlayer: MediaPlayer? = null
    private var selectedMusicPosition = 0

    lateinit var binding: ActivityMain3PostingNowUploadBinding
    private val imageResources = arrayOf(
        R.drawable.cd_music0,
        R.drawable.cd_music1,
        R.drawable.cd_music2,
        R.drawable.cd_music3,
        R.drawable.cd_music4,
        R.drawable.cd_music5,
        R.drawable.cd_music6,
    )

    private val musicResources = arrayOf(
        R.raw.music0_link_jim_yosef,
        R.raw.music1_on_and_on_cartoon,
        R.raw.music2_heroes_tonight_janji,
        R.raw.music3_my_heart_different_heaven_ehde,
        R.raw.music4_mortals_warriyo,
        R.raw.music5_sky_high_elektronomia,
        R.raw.music6_fearless_lost_sky,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3PostingNowUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 데이터 가져오기
        val selectedName = intent.getStringExtra("selected_name")
        val receivedPost = intent.getSerializableExtra("post_data") as? Post

        binding.relayName.text = "$selectedName"

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = AdapterMain3PostingNowUpload(this)
        recyclerView.adapter = adapter

        val editText = binding.editText
        editText.background = null;



        val leftArrow = binding.leftArrow
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val rightArrow = binding.rightArrow
        rightArrow.setOnClickListener {
            val nextIntent = Intent(this, Main1HomeFirstFragment::class.java)
            nextIntent.putExtra("selected_name", selectedName)
            nextIntent.putExtra("post_data", receivedPost)
            startActivity(nextIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.playButton.setOnClickListener {
            if (isRotating) {
                stopRotation()
                stopMusic()
            } else {
                startRotation()
                startMusic(selectedMusicPosition)
            }
        }
    }

    override fun onButtonClicked(position: Int) {
        selectedMusicPosition = position

        binding.mp3Song.visibility = View.VISIBLE
        binding.cdImageView.setImageResource(imageResources[position])
    }

    private fun startRotation() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                binding.cdImageView.rotation = animatedValue
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    isRotating = true
                }
            })
        }
        rotationAnimator?.start()
    }

    private fun stopRotation() {
        rotationAnimator?.cancel()
        binding.cdImageView.rotation = 0f
        isRotating = false
    }

    private fun startMusic(selectedPosition : Int) {
        if (!isPlaying) {
            mediaPlayer = MediaPlayer.create(this, musicResources[selectedPosition])
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun stopMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                reset()
                release()
                this@Main3PostingNowUploadActivity.isPlaying = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
