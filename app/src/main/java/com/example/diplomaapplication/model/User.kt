package com.example.diplomaapplication.model

data class User(
    var id: String = "",
    var firstName: String = "",
    var bio: String = "",
    var avatar: Int? = null,
    var isDoctor: Boolean = true,
    var medicineBranch: String? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var starCount: Float? = null
)