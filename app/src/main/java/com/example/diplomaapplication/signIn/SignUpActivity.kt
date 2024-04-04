//package com.example.diplomaapplication.signIn
//
//import android.app.AlertDialog
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.util.Patterns
//import android.view.View
//import android.widget.Toast
//import com.example.diplomaapplication.R
//import com.example.diplomaapplication.databinding.ActivitySignUpBinding
//import com.example.diplomaapplication.databinding.FragmentRegisterBinding
//import com.example.diplomaapplication.helpers.Helpers
//import com.example.diplomaapplication.model.User
//
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//import java.lang.Exception
//
//class SignUpActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//    private lateinit var binding: FragmentRegisterBinding
//    private lateinit var user: User
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = FragmentRegisterBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = Firebase.auth
//
//    }
//    fun registerWithEmailAndPassword(email: String, password: String, view: View, user: User) {
//        if (::binding.isInitialized && checkAllField()) {
//            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Вы вошли!", Toast.LENGTH_SHORT).show()
//
//                    val intent = Intent(this, ProfilePageActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    Log.e("Ошибка", task.exception.toString())
//                }
//            }
//        }
//    }
//
//
//    private fun checkAllField(): Boolean {
//        // Check if binding is initialized
//        if (!::binding.isInitialized) {
//            return false
//        }
//        val email = binding.userEmailInput.text.toString()
//        if (binding.userEmailInput.text.toString() == "") {
//            binding.userEmailInput.error = "Заполните это поле"
//            return false
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.userEmailInput.error = "Почта указано неправильно"
//            return false
//        }
//        if (binding.userPasswordInput.text.toString() == "") {
//            binding.userPasswordInput.error = "Заполните это поле"
//            return false
//        }
//        if (binding.userPasswordInput.length() < 8) {
//            binding.userPasswordInput.error = "Ваш пароль должна содержать не менее 8 символов"
//            return false
//        }
//        if (binding.userConfirmPasswordInput.text.toString() == "") {
//            binding.userConfirmPasswordInput.error = "Заполните это поле"
//            return false
//        }
//        if (binding.userPasswordInput.text.toString() != binding.userConfirmPasswordInput.text.toString()) {
//            binding.userConfirmPasswordInput.error = "Пароли не совпадают"
//            return false
//        }
//        return true
//    }
//
//}