package com.example.diplomaapplication.ui.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diplomaapplication.R
import com.example.diplomaapplication.ui.map.util.NetworkConstants

class PhotoRecyclerAdapter(private var photoIdList: List<String>)
    : RecyclerView.Adapter<PhotoRecyclerAdapter.PhotoViewHolder>() {

    fun updatePhotoList(newIDs: List<String>) {
        photoIdList = newIDs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        PhotoViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false))

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (photoIdList[position] == "empty")
            holder.photoImageView.setImageResource(R.drawable.ic_baseline_photo_24)
        else {
            holder.apply {
                Glide.with(itemView)
                    .load(createPhotoUrl(refID = photoIdList[position], maxHeight = "400"))
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .into(photoImageView)
            }
        }
    }

    override fun getItemCount(): Int = photoIdList.size

    private fun createPhotoUrl(refID: String, maxWidth: String = "", maxHeight: String = ""): String =
        NetworkConstants.BASE_URL + NetworkConstants.PLACE_PHOTO_REQUEST_PATH +
            String.format("?%s=%s&%s=%s&%s=%s&%s=%s",
                NetworkConstants.KEY_KEY, NetworkConstants.KEY_VALUE,
                NetworkConstants.PHOTO_REFERENCE_KEY, refID,
                NetworkConstants.MAX_WIDTH_KEY, maxWidth,
                NetworkConstants.MAX_HEIGHT_KEY, maxHeight)

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var photoImageView: ImageView = itemView.findViewById(R.id.photo_imageView)
    }
}