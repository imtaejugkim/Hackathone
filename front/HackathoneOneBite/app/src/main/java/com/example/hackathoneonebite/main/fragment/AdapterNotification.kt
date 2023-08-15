package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterNotification : RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>() {
    private val items: MutableList<NotificationItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_relay_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: NotificationItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notificationProfile: ImageView = itemView.findViewById(R.id.notificationProfile)
        private val notificationName: TextView = itemView.findViewById(R.id.notificationName)
        private val notificationTime: TextView = itemView.findViewById(R.id.notificationTime)
        private val notificationButton: Button = itemView.findViewById(R.id.notificationButton)

        fun bind(item: NotificationItem) {
            notificationName.text = item.userName
            notificationTime.text = item.remainingTime
            if(item.remainingTime == null){
                notificationButton.visibility = View.GONE
            }
            notificationProfile.setImageResource(R.drawable.test_image1)
        }

        init {
            notificationButton.setOnClickListener {
                val context = itemView.context
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = items[position]
                    if (clickedItem.remainingTime == null) {
                        val userId = clickedItem.posId // 수정해야 할 부분
                        val intent = Intent(context, Main3PostingMakingActivity::class.java)
                        intent.putExtra("userId", userId)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

}

data class NotificationItem(
    val id: Long?,
    val userName: String?,
    val message: String?,
    val remainingTime: String?,
    val posId: Long?
)
