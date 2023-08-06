package com.example.hackathoneonebite.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R

class Main3PostingMakingActivity : AppCompatActivity() {

    private var contentFramesFilled = booleanArrayOf(false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_making)

        // Intent에서 클릭된 레이아웃의 ID를 가져옴
        val clickedLayoutId = intent.getIntExtra("layout_id",0)

        // 클릭된 레이아웃의 ID에 따라 해당 레이아웃을 보여주도록 처리
        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        when (clickedLayoutId) {
            0 -> {
                postImageLayoutFilm.visibility = View.VISIBLE
                postImageLayout1.visibility = View.INVISIBLE
                postImageLayout2.visibility = View.INVISIBLE
                contentFramePseudo()
            }
            1 -> {
                postImageLayoutFilm.visibility = View.INVISIBLE
                postImageLayout1.visibility = View.VISIBLE
                postImageLayout2.visibility = View.INVISIBLE
                contentFrame1()

            }
            2 -> {
                postImageLayoutFilm.visibility = View.INVISIBLE
                postImageLayout1.visibility = View.INVISIBLE
                postImageLayout2.visibility = View.VISIBLE
                contentFrame2()
            }
        }

        val relayButton = findViewById<Button>(R.id.relayButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)

        updateButtonsVisibility()

        relayButton.setOnClickListener {

        }

        uploadButton.setOnClickListener {

        }

    }

    private fun updateButtonsVisibility() {
        val relayButton = findViewById<Button>(R.id.relayButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)

        if (contentFramesFilled.all { it }) {
            relayButton.visibility = View.GONE
            uploadButton.visibility = View.VISIBLE
        } else {
            relayButton.visibility = View.VISIBLE
            uploadButton.visibility = View.GONE
        }
    }



    private fun contentFramePseudo() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame1)
        val img2 = findViewById<ImageView>(R.id.imageView2frame1)
        val img3 = findViewById<ImageView>(R.id.imageView3frame1)
        val img4 = findViewById<ImageView>(R.id.imageView4frame1)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                contentFramesFilled[index] = true
                updateButtonsVisibility()
            }
        }
    }

    private fun contentFrame1() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame1)
        val img2 = findViewById<ImageView>(R.id.imageView2frame1)
        val img3 = findViewById<ImageView>(R.id.imageView3frame1)
        val img4 = findViewById<ImageView>(R.id.imageView4frame1)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                contentFramesFilled[index] = true
                updateButtonsVisibility()
            }
        }
    }

    private fun contentFrame2() {
        val img1 = findViewById<ImageView>(R.id.imageView1frame2)
        val img2 = findViewById<ImageView>(R.id.imageView2frame2)
        val img3 = findViewById<ImageView>(R.id.imageView3frame2)
        val img4 = findViewById<ImageView>(R.id.imageView4frame2)

        val contents = arrayOf(img1, img2, img3, img4)

        for ((index, layout) in contents.withIndex()) {
            layout.setOnClickListener {
                val intent = Intent(this@Main3PostingMakingActivity, Main3PostingSelectActivity::class.java)
                intent.putExtra("contents_id", index)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                contentFramesFilled[index] = true
                updateButtonsVisibility()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_SELECT_IMAGE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImagePath = data?.getStringExtra("selected_image_path")
            val contentsId = data?.getIntExtra("contents_id", 2) ?: 2
            if (selectedImagePath != null) {
                val selectedImageView = findImageViewForCurrentContentFrame(contentsId)
                Glide.with(this)
                    .load(selectedImagePath)
                    .into(selectedImageView)
            }
        }
    }

    private fun findImageViewForCurrentContentFrame(contentsId: Int): ImageView {
        val postImageLayoutFilm = findViewById<View>(R.id.postImageLayoutFilm)

        return when (contentsId) {
            0 -> {
                postImageLayoutFilm.findViewById(R.id.imageView1frame1)
            }

            1 -> {
                postImageLayoutFilm.findViewById(R.id.imageView2frame1)
            }

            2 -> {
                postImageLayoutFilm.findViewById(R.id.imageView3frame1)
            }

            3 -> {
                postImageLayoutFilm.findViewById(R.id.imageView4frame1)
            }

            else -> throw IllegalArgumentException("Invalid clickedLayoutId")
        }
    }


}