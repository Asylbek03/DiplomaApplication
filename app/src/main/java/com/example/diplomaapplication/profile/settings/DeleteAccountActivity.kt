package com.example.diplomaapplication.profile.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.diplomaapplication.databinding.ActivityDeleteAccountBinding
import com.example.diplomaapplication.views.auth.LoginFragment
import com.google.firebase.auth.FirebaseAuth

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()

        binding.btnDeleteAccount.setOnClickListener {
            val email = binding.userEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                val user = auth.currentUser
                if (user != null && user.email == email) {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Аккаунт удален!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginFragment::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Ошибка при удалении аккаунта: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Введенная почта не соответствует текущему пользователю", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Введите вашу почту", Toast.LENGTH_SHORT).show()
            }
        }

    }
}