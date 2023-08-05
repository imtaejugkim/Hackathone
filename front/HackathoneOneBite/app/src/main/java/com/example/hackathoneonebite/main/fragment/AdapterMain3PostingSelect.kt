package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.hackathoneonebite.R

class AdapterMain3PostingSelect(
    private val photoList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AdapterMain3PostingSelect.PhotoViewHolder>() {

    var selectedPosition: Int = RecyclerView.NO_POSITION // Variable to store the selected position, initially set to NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPath = photoList[position]
        val options = com.bumptech.glide.request.RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(holder.itemView.context)
            .load(photoPath)
            .apply(options)
            .into(holder.photoImageView)

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(photoPath)
        }

        holder.itemView.isSelected = selectedPosition == position
    }

    override fun getItemCount(): Int = photoList.size

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }
}
