//package com.example.diplomaapplication.signIn
//
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.util.Patterns
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import androidx.appcompat.app.AppCompatActivity
//import com.example.diplomaapplication.R
//import com.example.diplomaapplication.databinding.ActivitySignInBinding
//import com.example.diplomaapplication.databinding.FragmentLoginBinding
//import androidx.navigation.fragment.findNavController
//import com.example.diplomaapplication.databinding.FragmentRegisterBinding
//import com.example.diplomaapplication.ui.profile.ProfileFragment
//import com.example.diplomaapplication.views.auth.RegisterFragment
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//
//import java.lang.Exception
//
//
//class SignInActivity : AppCompatActivity() {
//
//    //private lateinit var auth: FirebaseAuth
//    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private lateinit var binding: FragmentLoginBinding
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = FragmentLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        supportActionBar?.hide()
//
//        //auth = FirebaseAuth.getInstance()
//
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
////        binding.tvSignInWithGoogle.setOnClickListener {
////            val intent = googleSignInClient.signInIntent
////            startActivityForResult(intent,10001)
////        }
////
////        binding.tvCreateAccount.setOnClickListener {
////            val intent = Intent(this, SignUpActivity::class.java)
////            startActivity(intent)
////            finish()
////        }
////
////        binding.tvForgotPassword.setOnClickListener {
////            val intent = Intent(this, ForgotPasswordActivity::class.java)
////            startActivity(intent)
////            finish()
////        }
//
//        binding.signInButton.setOnClickListener {
//            loginWithEmailAndPassword(
//                binding.loginEmailInput.text.toString(),
//                binding.loginPasswordInput.text.toString()
//            )
//        }
//        setupNavigation()
//    }
//    private fun setupNavigation() {
//        binding.loginBackButton.setOnClickListener {
//            val intent = Intent(this, RegisterFragment::class.java)
//            startActivity(intent)
//
//        }
//    }
//
//    fun loginWithEmailAndPassword(email: String, password: String) {
//        if (::binding.isInitialized && checkAllField()) {
//            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Вы вошли!", Toast.LENGTH_SHORT).show()
//
//                    val intent = Intent(this, ProfileFragment::class.java)
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
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode==10001){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            val account = task.getResult(ApiException::class.java)
//            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
//            FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener{task->
//                    if(task.isSuccessful){
//
//                        val i  = Intent(this, ProfileFragment::class.java)
//                        startActivity(i)
//
//                    }else{
//                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//        }
//    }
//    fun getCurrentUserId(): String? {
//        return try {
//            auth.currentUser?.uid
//        } catch (ex: Exception) {
//            null
//        }
//    }
//    fun handleAuthStateChanged(actionLogIn: () -> Unit, actionLogOut: () -> Unit) {
//        auth.addAuthStateListener { firebaseAuth ->
//            if (firebaseAuth.currentUser == null) actionLogOut()
//            else actionLogIn()
//        }
//    }
//    private fun checkAllField(): Boolean {
//        val email = binding.loginEmailInput.text.toString()
//        if (binding.loginEmailInput.text.toString() == "") {
//            binding.loginEmailInput.error = "Заполните это поле"
//            return false
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.loginEmailInput.error = "Почта указано неправильно"
//            return false
//        }
//        if (binding.loginPasswordInput.text.toString() == "") {
//            binding.loginPasswordInput.error = "Заполните это поле"
//            return false
//        }
//        if (binding.loginPasswordInput.length() < 8) {
//            binding.loginPasswordInput.error = "Ваш пароль должна содержать не менее 8 символов"
//            return false
//        }
//        return true
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if(FirebaseAuth.getInstance().currentUser != null){
//            val i  = Intent(this, ProfileFragment::class.java)
//            startActivity(i)
//        }
//    }
//
//
//
//}
