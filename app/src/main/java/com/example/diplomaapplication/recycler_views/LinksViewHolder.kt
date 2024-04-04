package com.example.diplomaapplication.recycler_views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class LinksViewHolder(v:View):RecyclerView.ViewHolder(v) {
    val linkText: TextView = v.findViewById(R.id.linkText)
}