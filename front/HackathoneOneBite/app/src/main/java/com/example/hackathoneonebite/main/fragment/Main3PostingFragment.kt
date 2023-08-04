package com.example.hackathoneonebite.main.fragment

import android.Manifest
import android.content.ContentUris
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R

class Main3PostingFragment : Fragment() {

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: AdapterMain3Posting
    private val photoList = mutableListOf<String>()
    private lateinit var selectedImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main3_posting, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.layoutManager = layoutManager

        adapter = AdapterMain3Posting(photoList) { photoPath ->
            // 아이템이 클릭되면 해당 사진을 위의 ConstraintLayout에 표시
            Glide.with(requireContext())
                .load(photoPath)
                .into(selectedImageView)
        }
        recyclerView.adapter = adapter

        selectedImageView = view.findViewById(R.id.selectedImageView)

        return view
    }



    override fun onStart() {
        super.onStart()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isPermissionGranted = prefs.getBoolean("permission_granted", false)

        if (isPermissionGranted) {
            loadPhotos()
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 이미 이전에 권한이 허용되었지만, preference가 업데이트되지 않았을 수 있으므로
                // 지금은 preference를 업데이트하고 사진을 로드
                prefs.edit().putBoolean("permission_granted", true).apply()
                loadPhotos()
            } else {
                requestExternalStoragePermission()
            }
        }
    }

    private fun requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    private fun loadPhotos() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor: Cursor? = requireActivity().contentResolver.query(
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
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용, SharedPreferences를 업데이트
                val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                prefs.edit().putBoolean("permission_granted", true).apply()
                loadPhotos()
            } else {
                // 권한이 거부된 경우 처리
            }
        }
    }

}
