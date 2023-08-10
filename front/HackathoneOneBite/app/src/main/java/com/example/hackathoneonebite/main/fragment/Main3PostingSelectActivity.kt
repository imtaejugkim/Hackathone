package com.example.hackathoneonebite.main.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R
import java.io.ByteArrayOutputStream

class Main3PostingSelectActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var matrix: Matrix
    private var scaleFactor = 1.0f
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: AdapterMain3PostingSelect
    private val photoList = mutableListOf<String>()
    private lateinit var selectedImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_select)

        val clickedContentsId = intent.getIntExtra("contents_id", 0)
        val clickedLayoutId = intent.getIntExtra("layout_id", 0)
        Log.d("tag", clickedLayoutId.toString())

        val imageTheme2Frame1 = findViewById<View>(R.id.selectedImageView2Frame1)
        val imageTheme2Frame3 = findViewById<View>(R.id.selectedImageView2Frame3)
        val imageTheme2Frame4 = findViewById<View>(R.id.selectedImageView2Frame4)
        val imageThemeBasic = findViewById<View>(R.id.selectedImageViewBasic)


        when (clickedLayoutId) {
            in 0..1 -> {
                imageThemeBasic.visibility = View.VISIBLE
                imageTheme2Frame1.visibility = View.INVISIBLE
                imageTheme2Frame3.visibility = View.INVISIBLE
                imageTheme2Frame4.visibility = View.INVISIBLE
                selectedImageView = findViewById(R.id.selectedImageViewBasic)
            }
            2 -> {
                when (clickedContentsId) {
                    in 0..1 -> {
                        imageThemeBasic.visibility = View.INVISIBLE
                        imageTheme2Frame1.visibility = View.VISIBLE
                        imageTheme2Frame3.visibility = View.INVISIBLE
                        imageTheme2Frame4.visibility = View.INVISIBLE
                        selectedImageView = findViewById(R.id.selectedImageView2Frame1)

                    }
                    2 -> {
                        imageThemeBasic.visibility = View.INVISIBLE
                        imageTheme2Frame1.visibility = View.INVISIBLE
                        imageTheme2Frame3.visibility = View.VISIBLE
                        imageTheme2Frame4.visibility = View.INVISIBLE
                        selectedImageView = findViewById(R.id.selectedImageView2Frame3)
                    }
                    3 -> {
                        imageThemeBasic.visibility = View.INVISIBLE
                        imageTheme2Frame1.visibility = View.INVISIBLE
                        imageTheme2Frame3.visibility = View.INVISIBLE
                        imageTheme2Frame4.visibility = View.VISIBLE
                        selectedImageView = findViewById(R.id.selectedImageView2Frame4)
                    }
                }
            }
        }

        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = GridLayoutManager(this, 4)
        recyclerView.layoutManager = layoutManager

        adapter = AdapterMain3PostingSelect(photoList) { photoPath ->
            // Item click handling
            Glide.with(this)
                .load(photoPath)
                .into(selectedImageView)
        }
        recyclerView.adapter = adapter

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isPermissionGranted = prefs.getBoolean("permission_granted", false)

        if (isPermissionGranted) {
            loadPhotos()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                prefs.edit().putBoolean("permission_granted", true).apply()
                loadPhotos()
            } else {
                requestExternalStoragePermission()
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val selectedDrawable = selectedImageView.drawable as BitmapDrawable?
            val selectedBitmap = selectedDrawable?.bitmap

            if (selectedBitmap != null) {
                val stream = ByteArrayOutputStream()
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byteArray = stream.toByteArray()

                val resultIntent = Intent()
                resultIntent.putExtra("selected_image", byteArray)
                Log.e("잘려진 사진 크기", byteArray.toString())

                resultIntent.putExtra("contents_id", clickedContentsId)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        matrix = Matrix()


        selectedImageView.setOnTouchListener(View.OnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@OnTouchListener true
        })

        val leftArrow = findViewById<ImageView>(R.id.leftArrow)
        leftArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            // 최대/최소 확대 비율 설정 (필요에 따라 조정)
            scaleFactor = scaleFactor.coerceIn(0.1f, 5.0f)

            // 확대/축소를 위한 Matrix 설정
            matrix.setScale(scaleFactor, scaleFactor)
            selectedImageView.imageMatrix = matrix

            return true
        }
    }


    private fun requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    private fun loadPhotos() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val imageId = it.getLong(idColumn)
                val imagePath = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                ).toString()
                photoList.add(0,imagePath)
                Log.d("이미지 경로 값",imagePath)

            }
            adapter.notifyDataSetChanged()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                prefs.edit().putBoolean("permission_granted", true).apply()
                loadPhotos()
            } else {
                // Handle permission denial here
            }
        }
    }


}
