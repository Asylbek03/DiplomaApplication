package com.example.diplomaapplication.views.doctor

import com.example.diplomaapplication.model.Request

interface ChatInterface {

    fun onRequestChanged(request: Request)

    fun onDeleteChat(requestId: String)

    fun onDoctorLeave(request: Request)

    fun onPatientDisabledChat()

}