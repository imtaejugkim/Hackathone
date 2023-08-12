package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterMain3PostingUpload(private val buttonClickListener: OnButtonClickListener) :
    RecyclerView.Adapter<AdapterMain3PostingUpload.ButtonViewHolder>() {

    private val buttonCount = 7
    private val buttonStates = Array(buttonCount) { false } // 초기 상태는 모두 false
    private var lastClickedPosition = -1

    interface OnButtonClickListener {
        fun onButtonClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music_button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val buttonName = when (position) {
            0 -> "link jim yosef"
            1 -> "on and on cartoon"
            2 -> "heroes tonight janji"
            3 -> "my_heart_different_heaven ehde"
            4 -> "mortals warriyo.mp3"
            5 -> "sky high elektronomia.mp3"
            6 -> "fearless lost sky.mp3"
            else -> "기본 버튼 이름"
        }
        holder.bind(buttonName, position)
    }

    override fun getItemCount(): Int {
        return buttonCount
    }

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView.findViewById<Button>(R.id.button)

        fun bind(buttonText: String, position: Int) {
            button.text = buttonText
            button.setBackgroundResource(if (buttonStates[position]) R.drawable.button_background_highlight else R.drawable.button_background_light_gray)
            button.setOnClickListener {
                if (lastClickedPosition != -1 && lastClickedPosition != position) {
                    buttonStates[lastClickedPosition] = false // 이전에 눌린 버튼 상태 변경
                    notifyItemChanged(lastClickedPosition) // 변경된 상태로 갱신
                }

                buttonStates[position] = !buttonStates[position] // 현재 버튼 상태 반전
                notifyItemChanged(position) // 변경된 상태로 갱신
                lastClickedPosition = position // 현재 버튼을 이전에 눌린 버튼으로 설정
                buttonClickListener.onButtonClicked(position)
            }
        }
    }
}
