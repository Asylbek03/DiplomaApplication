package com.example.diplomaapplication.signIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.diplomaapplication.profile.ProfilePageActivity
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()
            if (checkAllField()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Вы вошли!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, ProfilePageActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("Ошибка", task.exception.toString())
                    }
                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.tvSignInWithGoogle.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent,10001)
        }

        binding.tvCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==10001){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){

                        val i  = Intent(this, ProfilePageActivity::class.java)
                        startActivity(i)

                    }else{
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    private fun checkAllField(): Boolean {
        val email = binding.userEmail.text.toString()
        if (binding.userEmail.text.toString() == "") {
            binding.inputEmailLayout.error = "Заполните это поле"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmailLayout.error = "Почта указано неправильно"
            return false
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

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            val i  = Intent(this, ProfilePageActivity::class.java)
            startActivity(i)
        }
    }
}
