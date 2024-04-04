package com.example.diplomaapplication.authentication

import android.app.AlertDialog
import android.util.Log
import android.view.View
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class Authentication: DatabaseError {

    // Google Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    // Firebase Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val database = FireStoreDatabase()

    // Helpers (show dialogs, snackbars, etc.)
    private val helpers: Helpers = Helpers()

    fun registerWithEmailAndPassword(email: String, password: String,view: View,user: User) {
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Регистрация")
        try {
            dialog.show()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //change user id to the current user uid in authentication
                    user.id = task.result!!.user!!.uid
                    //insert user to database
                    database.insertUserToDatabase(user,view,this)
                    helpers.showSnackBar("Successfully registering",view)
                }else{
                    //catch network exception etc
                    helpers.showSnackBar(task.exception!!.message.toString(),view)
                }
                dialog.dismiss()
            }
        }catch (ex:Exception){
            dialog.dismiss()
            helpers.showSnackBar(ex.message.toString(),view)
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String, view: View) {
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Logging")  // Loading dialog
        try {
            dialog.show()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    helpers.showSnackBar("Successfully logged in", view)
                } else {
                    helpers.showSnackBar("Login failed: ${task.exception?.message}", view)
                }
                dialog.dismiss()
            }
        } catch (ex: Exception) {
            dialog.dismiss()
            helpers.showSnackBar(ex.message.toString(), view)
        }
    }

    fun handleAuthStateChanged(actionLogIn: () -> Unit, actionLogOut: () -> Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) actionLogOut()
            else actionLogIn()
        }
    }

    fun signOutFromFirebase(view: View) {
        try {
            helpers.showSnackBar("Logged out", view)
            auth.signOut()
        } catch (ex: Exception) {
            helpers.showSnackBar(ex.message.toString(), view)
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            auth.currentUser?.uid
        } catch (ex: Exception) {
            null
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, view: View, complete: () -> Unit) {
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Changing password...")  // Loading dialog
        try {
            dialog.show()
            val user = auth.currentUser
            if (user != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                helpers.showSnackBar("Password has been changed", view)
                                complete()
                            } else {
                                Log.d("EXC", task2.exception.toString())
                                helpers.showSnackBar(task2.exception?.message ?: "Failed to change password", view)
                            }
                        }
                    } else {
                        Log.d("EXC", task1.exception.toString())
                        helpers.showSnackBar(task1.exception?.message ?: "Failed to reauthenticate", view)
                    }
                    dialog.dismiss()
                }
            } else {
                helpers.showSnackBar("Failed to get current user", view)
                dialog.dismiss()
            }
        } catch (ex: Exception) {
            dialog.dismiss()
            helpers.showSnackBar(ex.message.toString(), view)
        }
    }

    override fun errorHandled(errorMessage: String, view: View) {
        helpers.showSnackBar(errorMessage,view)
        if(auth.currentUser!=null)
            auth.currentUser!!.delete()
    }
}
