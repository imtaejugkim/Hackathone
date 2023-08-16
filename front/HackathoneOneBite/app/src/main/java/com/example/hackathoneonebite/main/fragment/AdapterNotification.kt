package com.example.hackathoneonebite.main.fragment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.R

class AdapterNotification(private val notificationList: List<NotificationItem>) :
    RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>() {

    private var requestNumber : Int = 1
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationName: TextView = itemView.findViewById(R.id.notificationName)
        val notificationTime: TextView = itemView.findViewById(R.id.notificationTime)
        val notificationButton: Button = itemView.findViewById(R.id.notificationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relay_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notificationList[position]

        holder.notificationName.text = item.message
        holder.notificationTime.text = item.createdAt
        Log.d("notificationNme",holder.notificationName.text.toString())
        if (item.remainingTime == null) {
            holder.notificationButton.isEnabled = false
            holder.notificationButton.visibility = View.GONE
            holder.notificationTime.visibility = View.GONE
        } else {
            holder.notificationButton.isEnabled = true
            holder.notificationButton.text = "수락"
            holder.notificationButton.visibility = View.VISIBLE
            holder.notificationTime.visibility = View.VISIBLE
        }

        holder.notificationButton.setOnClickListener {
            val context = holder.itemView.context
            Log.d("context",context.toString())
            val intent = Intent(context, Main3PostingMakingActivity::class.java)
            intent.putExtra("postId", item.postId)
            intent.putExtra("requestNumber",requestNumber)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }
}


data class NotificationItem(
    val id: Long,
    val userId: String,
    val message: String,
    val createdAt: String,
    val userName: String,
    val remainingTime: String?,
    val postId: Long
)