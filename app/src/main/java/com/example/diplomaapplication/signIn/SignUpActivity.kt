package com.example.diplomaapplication.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.diplomaapplication.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnSignUp.setOnClickListener{
            val email = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()
            if(checkAllField()){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        auth.signOut()
                        Toast.makeText(this, "Аккаунт успешно создан!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.e("Ошибка", it.exception.toString())
                    }
                }
            }
        }
    }

    private fun checkAllField(): Boolean{
        val email = binding.userEmail.text.toString()
        if(binding.userEmail.text.toString() == ""){
            binding.inputEmailLayout.error = "Заполните это поле"
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputEmailLayout.error = "Почта указано неправильно"
            return false
        }
        if(binding.userPassword.text.toString()==""){
            binding.inputPasswordLayout.error="Заполните это поле"
            binding.inputPasswordLayout.errorIconDrawable = null
            return false
        }
        if(binding.userPassword.length()<8){
            binding.inputPasswordLayout.error="Ваш пароль должна содержать не менее 8 символов"
            binding.inputPasswordLayout.errorIconDrawable = null
            return false
        }
        if(binding.userConfirmPassword.text.toString()==""){
            binding.inputConfirmPasswordLayout.error="Заполните это поле"
            binding.inputConfirmPasswordLayout.errorIconDrawable = null
            return false
        }
        if(binding.userPassword.text.toString() != binding.userConfirmPassword.text.toString()){
            binding.inputPasswordLayout.error = "Пароли не совпадают"
        }
        return true
    }
}