package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterMain2SearchThemeFilm: RecyclerView.Adapter<AdapterMain2SearchThemeFilm.ViewHolder>() {

    private val postCount = 5 // 출력할 post_frame 개수 (임의 조정)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme_film, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // View 요소 초기화
        val touchView = holder.itemView.findViewById<View>(R.id.touchView)
        val imageView1 = holder.itemView.findViewById<ImageView>(R.id.imageView1frame1)
        val imageView2 = holder.itemView.findViewById<ImageView>(R.id.imageView2frame1)
        val imageView3 = holder.itemView.findViewById<ImageView>(R.id.imageView3frame1)
        val imageView4 = holder.itemView.findViewById<ImageView>(R.id.imageView4frame1)
        val relayTextView = holder.itemView.findViewById<ImageView>(R.id.RELAYTextView)
        val dateTextView = holder.itemView.findViewById<TextView>(R.id.dateTextView)

        // 아래와 같이 이미지 리소스를 설정할 수 있습니다.
        imageView1.setImageResource(R.drawable.test_image1)
        imageView2.setImageResource(R.drawable.test_image1)
        imageView3.setImageResource(R.drawable.test_image1)
        imageView4.setImageResource(R.drawable.test_image1)

        // 이미지 리소스를 설정하는 방식에 따라서 아래와 같이 처리할 수 있습니다.
        // relayTextView.setImageResource(R.drawable.img_logo_white)

        // dateTextView.text = "2023.02.04"와 같이 텍스트를 설정할 수 있습니다.
    }


    override fun getItemCount(): Int {
        return postCount
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder 내부에서 뷰 요소에 대한 참조를 설정하는 로직이 필요할 수 있음
    }
}
