package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterMain3PostingUpload : RecyclerView.Adapter<AdapterMain3PostingUpload.ButtonViewHolder>() {

    // 버튼의 개수를 나타내는 변수
    private val buttonCount = 7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music_button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.bind("Button ${position + 1}")
    }

    override fun getItemCount(): Int {
        return buttonCount
    }

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView.findViewById<Button>(R.id.button)

        fun bind(buttonText: String) {
            button.text = buttonText
        }
    }
}
