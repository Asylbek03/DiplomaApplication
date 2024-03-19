package com.example.diplomaapplication.profile.settings;

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomaapplication.profile.ProfilePageActivity
import com.example.diplomaapplication.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity:AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()

        binding.btnChangePassword.setOnClickListener{
            val user = auth.currentUser
            val oldPassword = binding.userPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            if(checkPasswordField() && user != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updatePasswordTask ->
                                    if (updatePasswordTask.isSuccessful) {
                                        Toast.makeText(this, "Пароль изменен успешно!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, ProfilePageActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    } else {
                                        Toast.makeText(this, "Ошибка при изменений пароля ${reAuthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Введенный пароль не соответствует текущему пользователю", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun checkPasswordField(): Boolean {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val oldPassword = binding.userPassword.text.toString()
        if (user != null && oldPassword != "") {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reAuthTask ->
                    if (!reAuthTask.isSuccessful) {
                        binding.inputPasswordLayout.error = "Неправильный старый пароль"
                        binding.inputPasswordLayout.errorIconDrawable = null
                    }
                }

        }

        if (binding.userPassword.text.toString() == "") {
            binding.inputPasswordLayout.error = "Заполните это поле"
            binding.inputPasswordLayout.errorIconDrawable = null
            return false
        }
        if (binding.userPassword.length() < 8) {
            binding.inputPasswordLayout.error = "Ваш пароль должна содержать не менее 8 символов"
            binding.inputPasswordLayout.errorIconDrawable = null
            return false
        }
        return true

    }
}
