package com.example.diplomaapplication.databases.firestore_database

import com.example.diplomaapplication.model.User


interface GetCurrentUserInterface {

    fun onGetCurrentUser(user: User)
}