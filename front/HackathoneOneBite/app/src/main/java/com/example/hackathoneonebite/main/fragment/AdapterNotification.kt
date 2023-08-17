package com.example.hackathoneonebite.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathoneonebite.R
import com.example.hackathoneonebite.api.Main5LoadProfileInfoResponse
import com.example.hackathoneonebite.api.RetrofitBuilder
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.Duration

class AdapterNotification(val context: Context, val notificationList: MutableList<NotificationItem>) :
    RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>() {

    val baseUrl: String = "http://203.252.139.231:8080/"
    private var requestNumber : Int = 1
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationName: TextView = itemView.findViewById(R.id.notificationName)
        val notificationBackText : TextView = itemView.findViewById(R.id.notificationBackText)
        val notificationButton: ImageView = itemView.findViewById(R.id.notificationButton)
        val profileImage: ImageView = itemView.findViewById(R.id.notificationProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relay_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notificationList[position]

        holder.notificationName.text = item.userName
        holder.notificationBackText.text = item.message

        Glide.with(context)
            .load(baseUrl + notificationList[position].profileUrl)
            .into(holder.profileImage)



        if (item.remainingTime == null) {
            holder.notificationButton.isEnabled = false
            holder.notificationButton.visibility = View.GONE
        } else {
            holder.notificationButton.isEnabled = true
            holder.notificationButton.visibility = View.VISIBLE
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
    val postId: Long,
    val profileUrl: String
    )