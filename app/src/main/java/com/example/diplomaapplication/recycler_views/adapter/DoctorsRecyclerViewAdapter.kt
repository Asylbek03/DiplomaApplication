package com.example.diplomaapplication.recycler_views.adapter

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.recycler_views.holder.DoctorsRecyclerViewHolder
import com.example.diplomaapplication.views.doctor.AllDoctorsInterface

import java.util.*
import kotlin.collections.ArrayList

class DoctorsRecyclerViewAdapter(private val listOfDoctors:ArrayList<User>, private val listener: AllDoctorsInterface) : RecyclerView.Adapter<DoctorsRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsRecyclerViewHolder {
        return DoctorsRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.doctor_card,parent,false))
    }

    override fun getItemCount(): Int {
        return listOfDoctors.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorsRecyclerViewHolder, position: Int) {
        val doctor = listOfDoctors[holder.adapterPosition]

        val c = Calendar.getInstance()
        c.timeInMillis = doctor.startTime!!
        val startTime = DateFormat.format("HH:mm",c).toString()
        c.timeInMillis = doctor.endTime!!
        val endTime = DateFormat.format("HH:mm",c).toString()

        holder.doctorName.text = doctor.firstName
        holder.doctorWorkTime.text = "$startTime-$endTime"
        holder.doctorStarCount.text = doctor.starCount.toString()
        holder.doctorType.text = doctor.medicineBranch
        doctor.avatar?.let { holder.doctorAvatar.setImageResource(it) }

        holder.allBox.setOnClickListener {
            listener.chooseDoctor(doctor)
        }

    }
}