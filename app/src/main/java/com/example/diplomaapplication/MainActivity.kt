package com.example.diplomaapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomaapplication.profile.ProfilePageActivity
import com.example.diplomaapplication.signIn.SignInActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        supportActionBar?.hide()

        auth = Firebase.auth

        Handler(Looper.getMainLooper()).postDelayed({

            val user = auth.currentUser

            if(user != null){
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)

                finish()
            } else {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)

                finish()
            }


        }, 3000)
    }
}
