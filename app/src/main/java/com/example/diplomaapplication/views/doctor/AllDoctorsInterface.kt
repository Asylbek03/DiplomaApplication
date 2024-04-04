package com.example.diplomaapplication.views.doctor

import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.User


interface AllDoctorsInterface {
    fun changeType(doctorType:String)

    fun chooseDoctor(doctor: User)

    fun onDoctorsDatabaseChanged(allDoctors: ArrayList<User>)

    fun onRequestsDatabaseChanged(allRequests: ArrayList<Request>)

    fun onRequestAccept(request: Request)

}