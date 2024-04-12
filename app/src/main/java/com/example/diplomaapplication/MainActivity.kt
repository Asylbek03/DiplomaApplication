package com.example.diplomaapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesDatabase
import com.example.diplomaapplication.views.WelcomeAuthFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        supportActionBar?.hide()
        getFCMToken()
        auth = Firebase.auth
        Handler(Looper.getMainLooper()).postDelayed({

            val user = auth.currentUser

            if(user != null){
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)

                finish()
            } else {
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)

                finish()
            }


        }, 3000)
    }

    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "Token устройства: $token")
            } else {
                Log.e(TAG, "Не удалось получить токен устройства")
            }
        }
    }
}
