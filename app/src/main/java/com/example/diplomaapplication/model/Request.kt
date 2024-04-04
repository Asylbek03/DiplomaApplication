package com.example.diplomaapplication.model

data class Request (val id:String = "",
                    val patient:User? = null,
                    val doctor: User? = null,
                    var isUserActive: Boolean = true,
                    var isDoctorActive: Boolean = false,
                    val messages:ArrayList<Message> = arrayListOf())