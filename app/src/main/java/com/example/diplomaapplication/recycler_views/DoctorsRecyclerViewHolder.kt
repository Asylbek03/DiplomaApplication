package com.example.diplomaapplication.recycler_views

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R


class DoctorsRecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val doctorName: TextView = v.findViewById(R.id.doctorName)
    val doctorAvatar: ImageView = v.findViewById(R.id.doctorAvatar)
    val doctorType: TextView = v.findViewById(R.id.doctorType)
    val doctorStarCount: TextView = v.findViewById(R.id.doctorStarCount)
    val doctorWorkTime: TextView = v.findViewById(R.id.doctorWorkTime)
    val allBox: LinearLayout = v.findViewById(R.id.allDoctorBox)
}