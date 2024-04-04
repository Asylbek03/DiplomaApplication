package com.example.diplomaapplication.model.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterTimeViewModel : ViewModel() {
    private val startTime = MutableLiveData<Long>()
    private val endTime = MutableLiveData<Long>()

    fun setStartTime(chooseStartTime:Long){
        this.startTime.value = chooseStartTime
    }

    fun setEndTime(chooseEndTime:Long){
        this.endTime.value = chooseEndTime
    }

    fun getStartTime():LiveData<Long> = startTime
    fun getEndTime():LiveData<Long> = endTime
}