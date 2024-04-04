//package com.example.diplomaapplication.profile
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.diplomaapplication.databinding.FragmentProfileBinding
//import com.example.diplomaapplication.signIn.SignInActivity
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//
//class ProfilePageActivity : AppCompatActivity() {
//
//    private lateinit var auth:FirebaseAuth
//    private lateinit var binding:FragmentProfileBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = FragmentProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = Firebase.auth
//
//        binding.btnSignOut.setOnClickListener{
//            auth.signOut()
//
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//
//            finish()
//        }
//
//    }
//}