package com.example.diplomaapplication.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.diplomaapplication.databinding.ActivityForgotPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnForgotPassword.setOnClickListener{
            val email = binding.userEmail.text.toString()
            if(checkEmail()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    Toast.makeText(this, "Отправлено на почту", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            }
        }
    }

    private fun checkEmail():Boolean{
        val email = binding.userEmail.text.toString()
        if (binding.userEmail.text.toString() == "") {
            binding.inputEmailLayout.error = "Заполните это поле"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmailLayout.error = "Почта указано неправильно"
            return false
        }
        return true
    }
}