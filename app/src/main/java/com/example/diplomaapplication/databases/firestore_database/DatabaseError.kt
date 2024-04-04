package com.example.diplomaapplication.databases.firestore_database

import android.view.View

interface DatabaseError {
    fun errorHandled(errorMessage:String,view: View)
}