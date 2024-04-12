package com.example.diplomaapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.diplomaapplication.analytics.AnalyticsEvents
import com.example.diplomaapplication.analytics.AnalyticsHelper
import com.example.diplomaapplication.authentication.Authentication
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesDatabase
import com.example.diplomaapplication.databinding.ActivityMainPageBinding
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.MainActivityViewModel
import javax.inject.Inject

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        //val navController = findNavController(R.id.nav_host_fragment_activity_main_page)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_page) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_medicines, R.id.allDoctorsFragment, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        handleUserAuthStateChanged()
        setBottomNavigationVisibility()
    }

    private fun handleUserAuthStateChanged() {
        val authentication: Authentication = Authentication()
        authentication.handleAuthStateChanged({ stateLogIn() }, { stateLogOut() })
    }

    private fun stateLogIn() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_page) as NavHostFragment
        val navController = navHostFragment.navController
        if(navController.currentDestination!!.id == R.id.welcomeAuthFragment || navController.currentDestination!!.id == R.id.loginFragment || navController.currentDestination!!.id == R.id.registerFragment)
            navController.navigate(R.id.fragment_medicines, null, getNavOptions())
    }


    private fun stateLogOut() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_page) as NavHostFragment
        val navController = navHostFragment.navController
        if (navController.currentDestination!!.id != R.id.welcomeAuthFragment)
            navController.navigate(R.id.welcomeAuthFragment, null, getNavOptions())

        val currentUserViewModel: CurrentUserViewModel = ViewModelProvider(this).get(
            CurrentUserViewModel::class.java)
        currentUserViewModel.setUser(null)
    }



    private fun getNavOptions(): NavOptions? {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(R.id.mobile_navigation, true)
            .build()
    }


    private fun setBottomNavigationVisibility() {
        val mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val bottomNav: BottomNavigationView = findViewById(R.id.navView)

        mainViewModel.bottomNavVisibility.observe(this, Observer {
            bottomNav.visibility = it
        })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_page) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_profile || destination.id == R.id.fragment_medicines || destination.id == R.id.allDoctorsFragment)
                mainViewModel.showBottomNavigation()
            else mainViewModel.hideBottomNavigation()
        }
    }


    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_page) as NavHostFragment
        val navController = navHostFragment.navController

        val exitChatButton: ImageView? = findViewById(R.id.exitChatButton)

        if (navController.currentDestination!!.id == R.id.allDoctorsFragment && exitChatButton != null) {
            exitChatButton.performClick()
        } else {
            super.onBackPressed()
        }
    }



}