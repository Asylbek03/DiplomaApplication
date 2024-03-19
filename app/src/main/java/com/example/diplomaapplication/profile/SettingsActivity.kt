package com.example.diplomaapplication.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.diplomaapplication.databinding.ActivitySettingsBinding
import com.example.diplomaapplication.profile.settings.ChangePasswordActivity
import com.example.diplomaapplication.profile.settings.DeleteAccountActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()

        binding.btnChangePassword.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnDeleteAccount.setOnClickListener{
            val intent = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}