package com.example.diplomaapplication.recycler_views

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class MedicinesViewHolder(v:View): RecyclerView.ViewHolder(v){
    val medicineName: TextView = v.findViewById(R.id.medicineName)
    val medicineTypeAndAmount: TextView = v.findViewById(R.id.medicineTypeAndAmount)
    val medicineTime: TextView = v.findViewById(R.id.medicineTime)
    val medicineImage: ImageView = v.findViewById(R.id.medicineImage)
    val medicineBox: LinearLayout = v.findViewById(R.id.medicineBox)
}