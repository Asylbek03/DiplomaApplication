package com.example.diplomaapplication.recycler_views.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class PatientsRecyclerViewHolder(v:View):RecyclerView.ViewHolder(v) {
    val patientName: TextView = v.findViewById(R.id.patientName)
    val patientAvatar: ImageView = v.findViewById(R.id.patientAvatar)
    val patientBio: TextView = v.findViewById(R.id.patientBio)
    val confirmButton: ImageView = v.findViewById(R.id.confirmButton)
}