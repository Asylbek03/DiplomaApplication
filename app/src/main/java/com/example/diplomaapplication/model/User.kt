package com.example.diplomaapplication.model

data class User(
    var id: String = "",
    var firstName: String = "",
    var age: Int? = null,
    var gender: String? = null,
    var bio: String = "",
    var avatar: Int? = null,
    var isDoctor: Boolean = true,
    var medicineBranch: String? = null,
    var startTime: Long? = null,
    var endTime: Long? = null,
    var starCount: Float? = null,
    var fcmToken: String? = null,
    var allergies: List<String>? = null,
    var bloodSugarLevel: Float? = null,
    var chronicIllnesses: List<String>? = null
)

//data class User(
//    var id: String = "",
//    var firstName: String = "",
//    var age: Int? = null,
//    var gender: String? = null,
//    var bio: String = "",
//    var avatar: Int? = null,
//    var isDoctor: Boolean = true,
//    var medicineBranch: String? = null,
//    var startTime: Long? = null,
//    var endTime: Long? = null,
//    var starCount: Float? = null,
//    var fcmToken: String? = null,
//    var allergies: List<String>? = null,
//    var bloodSugarLevel: Float? = null,
//    var chronicIllnesses: List<String>? = null
//)