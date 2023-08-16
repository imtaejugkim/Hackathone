package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterMain2SearchTheme1 : RecyclerView.Adapter<AdapterMain2SearchTheme1.ViewHolder>() {

    private val postCount = 5 // 출력할 post_frame 개수 (임의 조정)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 데이터를 가져와서 post_frame에 바인딩하는 로직을 구현해야 함
    }

    override fun getItemCount(): Int {
        return postCount
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder 내부에서 뷰 요소에 대한 참조를 설정하는 로직이 필요할 수 있음
    }
}
