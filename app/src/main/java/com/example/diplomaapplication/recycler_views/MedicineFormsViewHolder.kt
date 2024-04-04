package com.example.diplomaapplication.recycler_views

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class MedicineFormsViewHolder(v:View):RecyclerView.ViewHolder(v) {
    val allBox: LinearLayout = v.findViewById(R.id.full_box)
    val photo: ImageView = v.findViewById(R.id.photo)
    val description: TextView = v.findViewById(R.id.description)
}