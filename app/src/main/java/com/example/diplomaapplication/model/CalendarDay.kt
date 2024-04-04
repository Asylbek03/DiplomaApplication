package com.example.diplomaapplication.model

data class CalendarDay(val day:Int,
                       val month: Int,
                       val year:Int,
                       val dayLetter: String,
                       var isChoose:Boolean = false)