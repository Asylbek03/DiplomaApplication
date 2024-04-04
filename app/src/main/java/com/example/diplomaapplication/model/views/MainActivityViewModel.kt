package com.example.diplomaapplication.model.views

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private val isBottomNavVisible = MutableLiveData<Int>()
    val bottomNavVisibility : LiveData<Int> get() = isBottomNavVisible

    init {
        hideBottomNavigation()
    }

    fun showBottomNavigation() = isBottomNavVisible.postValue(View.VISIBLE)

    fun hideBottomNavigation() = isBottomNavVisible.postValue(View.GONE)

}