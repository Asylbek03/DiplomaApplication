package com.example.diplomaapplication.recycler_views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.views.doctor.AllDoctorsInterface


class PatientsRecyclerViewAdapter(private val listOfPatients:ArrayList<Request>, private val listener: AllDoctorsInterface): RecyclerView.Adapter<PatientsRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientsRecyclerViewHolder {
       return PatientsRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.patient_card,parent,false))
    }

    override fun getItemCount(): Int {
        return listOfPatients.size
    }

    override fun onBindViewHolder(holder: PatientsRecyclerViewHolder, position: Int) {
        val request = listOfPatients[holder.adapterPosition]

        holder.patientName.text = request.patient!!.firstName
        request.patient.avatar?.let { holder.patientAvatar.setImageResource(it) }
        holder.patientBio.text = request.patient.bio

        holder.confirmButton.setOnClickListener {
            listener.onRequestAccept(request)
        }
    }
}