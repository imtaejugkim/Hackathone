package com.example.hackathoneonebite.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R

class Main3PostingMakingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_making)

        // Intent에서 클릭된 레이아웃의 ID를 가져옴
        val clickedLayoutId = intent.getIntExtra("layout_id",0)

        // 클릭된 레이아웃의 ID에 따라 해당 레이아웃을 보여주도록 처리
        val postImageLayout_pseudo = findViewById<View>(R.id.postImageLayout_pseudo)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        when (clickedLayoutId) {
            0 -> {
                postImageLayout_pseudo.visibility = View.VISIBLE
                postImageLayout1.visibility = View.INVISIBLE
                postImageLayout2.visibility = View.INVISIBLE
                contentFramePseudo()
            }
            1 -> {
                postImageLayout_pseudo.visibility = View.INVISIBLE
                postImageLayout1.visibility = View.VISIBLE
                postImageLayout2.visibility = View.INVISIBLE
                contentFrame1()

            }
            2 -> {
                postImageLayout_pseudo.visibility = View.INVISIBLE
                postImageLayout1.visibility = View.INVISIBLE
                postImageLayout2.visibility = View.VISIBLE
                contentFrame2()
            }
        }

        val relayButton = findViewById<Button>(R.id.relayButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)

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
            val contentsId = intent.getIntExtra("contents_id",2)
            // 이제 선택한 이미지 경로가 있으므로 해당하는 constraintLayout에 이미지를 표시합니다.
            // 예를 들어, selectedImagePath가 null이 아니라면 해당 이미지를 ImageView에 로드합니다.
            if (selectedImagePath != null) {
                val selectedImageView = findImageViewForCurrentContentFrame(contentsId) // 현재 content frame에 따라 올바른 ImageView를 찾는 함수를 구현해야 합니다.
                Glide.with(this)
                    .load(selectedImagePath)
                    .into(selectedImageView)
            }
        }
    }

    private fun findImageViewForCurrentContentFrame(contentsId:Int): ImageView {
        val postImageLayout_pseudo = findViewById<View>(R.id.postImageLayout_pseudo)
        val postImageLayout1 = findViewById<View>(R.id.postImageLayout1)
        val postImageLayout2 = findViewById<View>(R.id.postImageLayout2)

        return when (contentsId) {
            0 -> postImageLayout_pseudo.findViewById(R.id.imageView1frame1)
            1 -> postImageLayout_pseudo.findViewById(R.id.imageView2frame1)
            2 -> postImageLayout_pseudo.findViewById(R.id.imageView3frame1)
            3 -> postImageLayout_pseudo.findViewById(R.id.imageView4frame1)
            else -> throw IllegalArgumentException("Invalid clickedLayoutId")
        }
    }


}