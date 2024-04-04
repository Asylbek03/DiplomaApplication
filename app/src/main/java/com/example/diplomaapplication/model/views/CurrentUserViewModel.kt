package com.example.diplomaapplication.model.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplomaapplication.model.User

class CurrentUserViewModel:ViewModel() {

    private val currentUserData = MutableLiveData<User?>()
    init {
        setUser(null)
    }

    fun setUser(currentUser:User?){
        this.currentUserData.value = currentUser
    }

    fun getUser():LiveData<User?> = currentUserData

}