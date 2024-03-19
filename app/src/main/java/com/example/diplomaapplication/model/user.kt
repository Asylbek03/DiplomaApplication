package com.example.diplomaapplication.model

import com.example.diplomaapplication.R

data class User(
    var id: String = "",
    var firstName: String = "",
    var secondName: String = "",
    var bio: String = "",
    //var avatar: Int = R.drawable.doctor_avatar_1,
    var isDoctor: Boolean = true,
    var medicineBranch: String? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var starCount: Float? = null
)