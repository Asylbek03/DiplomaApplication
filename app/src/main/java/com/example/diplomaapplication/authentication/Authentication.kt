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

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val database = FireStoreDatabase()
    private val helpers: Helpers = Helpers()

    fun registerWithEmailAndPassword(email: String, password: String,view: View,user: User) {
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Регистрация")
        try {
            dialog.show()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    user.id = task.result!!.user!!.uid
                    database.insertUserToDatabase(user,view,this)
                    helpers.showSnackBar("Регистрация успешно!",view)
                }else{
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
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Вход")
        try {
            dialog.show()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    helpers.showSnackBar("Вход выполнен", view)
                } else {
                    helpers.showSnackBar("Ошибка: ${task.exception?.message}", view)
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
            helpers.showSnackBar("Вы вышли", view)
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
        val dialog: AlertDialog = helpers.getLoadingDialog(view.context, "Меняем пароль...")
        try {
            dialog.show()
            val user = auth.currentUser
            if (user != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                helpers.showSnackBar("Пароль изменен!", view)
                                complete()
                            } else {
                                Log.d("EXC", task2.exception.toString())
                                helpers.showSnackBar(task2.exception?.message ?: "Ошибка", view)
                            }
                        }
                    } else {
                        Log.d("EXC", task1.exception.toString())
                        helpers.showSnackBar(task1.exception?.message ?: "Ошибка", view)
                    }
                    dialog.dismiss()
                }
            } else {
                helpers.showSnackBar("Ошибка пользователя", view)
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
