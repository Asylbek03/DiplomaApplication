package com.example.diplomaapplication.databases.firestore_database

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.views.doctor.AllDoctorsInterface
import com.example.diplomaapplication.views.doctor.ChatInterface
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class FireStoreDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val patientsCollection = firestore.collection("Patients")
    private val doctorsCollection = firestore.collection("Doctors")
    private val requestsCollection = firestore.collection("Requests")

    fun insertUserToDatabase(user: User, view: View, errorListener: DatabaseError) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

                user.fcmToken = token

                if (user.isDoctor) {
                    doctorsCollection.document(user.id.toString()).set(user).addOnFailureListener {
                        errorListener.errorHandled(it.message.toString(), view)
                    }
                } else {
                    user.medicineBranch = null
                    user.starCount = null
                    user.startTime = null
                    user.endTime = null
                    patientsCollection.document(user.id.toString()).set(user).addOnFailureListener {
                        errorListener.errorHandled(it.message.toString(), view)
                    }
                }
            } else {
                Log.e(TAG, "Не удалось получить токен устройства")
            }
        }
    }


    fun getActiveDoctors(view: View, listener: AllDoctorsInterface) {
        val arrayListOfDoctors = arrayListOf<User>()

        doctorsCollection.get().addOnSuccessListener { querySnapshot ->
            arrayListOfDoctors.clear()
            for (document in querySnapshot) {
                val doctor = document.toObject(User::class.java)
                arrayListOfDoctors.add(doctor)
            }
            listener.onDoctorsDatabaseChanged(arrayListOfDoctors)
        }.addOnFailureListener { exception ->
            exception.message?.let { Helpers().showSnackBar(it, view) }
        }
    }



    fun getUserById(view: View, id: String, listener: GetCurrentUserInterface) {
        var currentUser: User? = null
        patientsCollection.document(id).get().addOnSuccessListener { patientSnapshot ->
            if (patientSnapshot.exists()) {
                val patient = patientSnapshot.toObject(User::class.java)
                if (patient != null) {
                    currentUser = patient
                    listener.onGetCurrentUser(currentUser!!)
                }
            } else {
                doctorsCollection.document(id).get().addOnSuccessListener { doctorSnapshot ->
                    if (doctorSnapshot.exists()) {
                        val doctor = doctorSnapshot.toObject(User::class.java)
                        if (doctor != null) {
                            currentUser = doctor
                            listener.onGetCurrentUser(currentUser!!)
                        }
                    } else {
                        val errorMessage = "Пользователь не найден. Пожалуйста, проверьте идентификатор и повторите попытку."
                        Helpers().showSnackBar(errorMessage, view)
                    }
                }.addOnFailureListener { exception ->
                    exception.message?.let { Helpers().showSnackBar(it, view) }
                }
            }
        }.addOnFailureListener { exception ->
            exception.message?.let { Helpers().showSnackBar(it, view) }
        }
    }



    fun insertRequest(request: Request, view:View, errorListener: DatabaseError){
        requestsCollection.document(request.id).set(request).addOnFailureListener {
            errorListener.errorHandled(it.message.toString(), view)
        }
    }

    fun getRequests(view: View, listener: AllDoctorsInterface, doctorId: String) {
        val listOfRequests = arrayListOf<Request>()

        requestsCollection.whereEqualTo("doctor.id", doctorId).get()
            .addOnSuccessListener { querySnapshot ->
                listOfRequests.clear()
                for (document in querySnapshot) {
                    val request = document.toObject(Request::class.java)
                    listOfRequests.add(request)
                }
                listener.onRequestsDatabaseChanged(listOfRequests)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Helpers().showSnackBar(it, view) }
            }
    }




    fun updateDoctorStartTime(userId: String, startTime: Long) {
        doctorsCollection.document(userId).update("startTime", startTime)
    }

    fun updateDoctorEndTime(userId: String, endTime: Long) {
        doctorsCollection.document(userId).update("endTime", endTime)
    }
    fun getRequestById(view: View, requestId: String, listener: ChatInterface) {
        var request: Request? = null

        requestsCollection.document(requestId).get().addOnSuccessListener { documentSnapshot ->
            val rq = documentSnapshot.toObject(Request::class.java)
            if (rq != null && rq.id == requestId) {
                request = rq
                listener.onRequestChanged(request!!)
            } else {
                listener.onPatientDisabledChat()
            }
        }.addOnFailureListener { exception ->
            exception.message?.let { Helpers().showSnackBar(it, view) }
        }
    }

    fun getFCMTokenForPatient(patientId: String, callback: (String?) -> Unit) {
        patientsCollection.document(patientId).get().addOnSuccessListener { documentSnapshot ->
            val patient = documentSnapshot.toObject(User::class.java)
            val patientToken = patient?.fcmToken
            callback(patientToken)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting FCM token for patient: ${exception.message}")
            callback(null)
        }
    }

    fun deleteAndRemoveValueEventListenerFromRequest(requestId: String){
        requestsCollection.document(requestId).delete()
    }
}
