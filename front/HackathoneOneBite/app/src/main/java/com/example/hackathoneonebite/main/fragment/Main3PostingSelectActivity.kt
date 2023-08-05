package com.example.hackathoneonebite.main.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R

class Main3PostingSelectActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: AdapterMain3PostingSelect
    private val photoList = mutableListOf<String>()
    private lateinit var selectedImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3_posting_select)

        val clickedLayoutId = intent.getIntExtra("contents_id",0)

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

        selectedImageView = findViewById(R.id.selectedImageView)

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
            val selectedImagePath = photoList.getOrNull(adapter.selectedPosition)
            val resultIntent = Intent()
            val contentIntent = Intent()
            resultIntent.putExtra("selected_image_path", selectedImagePath)
            contentIntent.putExtra("contents_id", clickedLayoutId)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
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
                photoList.add(imagePath)
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
