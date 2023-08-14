package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

/*class AdapterNotification(private val items: List<NotificationItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LIKE_NOTIFICATION -> {
                val itemView = inflater.inflate(R.layout.item_like_notification, parent, false)
                LikeNotificationViewHolder(itemView)
            }
            TYPE_FOLLOW_NOTIFICATION -> {
                val itemView = inflater.inflate(R.layout.item_relay_notification, parent, false)
                FollowNotificationViewHolder(itemView)
            }
            // 다른 유형의 아이템에 대한 처리 추가
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is LikeNotificationViewHolder -> {
                // 좋아요 알림 아이템 뷰홀더 처리
            }
            is FollowNotificationViewHolder -> {
                // 팔로우 알림 아이템 뷰홀더 처리
            }
            // 다른 유형의 아이템에 대한 처리 추가
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item.notificationType) {
            NotificationType.LIKE -> TYPE_LIKE_NOTIFICATION
            NotificationType.FOLLOW -> TYPE_FOLLOW_NOTIFICATION
            // 다른 유형의 아이템에 대한 처리 추가
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 유형 식별을 위한 상수 정의
    companion object {
        private const val TYPE_LIKE_NOTIFICATION = 0
        private const val TYPE_FOLLOW_NOTIFICATION = 1
        // 다른 유형의 아이템에 대한 상수 정의
    }
}*/
