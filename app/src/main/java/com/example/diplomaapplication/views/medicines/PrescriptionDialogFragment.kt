package com.example.diplomaapplication.views.medicines

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentPrescribeMedicinesBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.Message
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage


class PrescriptionDialogFragment : DialogFragment(), DatabaseError {

    private lateinit var currentUserViewModel: CurrentUserViewModel
    private lateinit var requestViewModel: RequestViewModel
    private lateinit var request: Request
    private lateinit var medicineInfo: String

    data class ChatMessage(
        val text: String,
        val isButton: Boolean = false,
        val buttonText: String? = null
    )

    companion object {
        fun newInstance(currentUserViewModel: CurrentUserViewModel, requestViewModel: RequestViewModel, request: Request): PrescriptionDialogFragment {
            val fragment = PrescriptionDialogFragment()
            fragment.currentUserViewModel = currentUserViewModel
            fragment.requestViewModel = requestViewModel
            fragment.request = request
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentPrescribeMedicinesBinding.inflate(inflater, container, false)
        val view = binding.root
        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_prescribe_medicines, null)

        builder.setView(view)
            .setTitle("Назначить лекарство")
            .setPositiveButton("Отправить") { dialog, which ->
                val medicineName = view.findViewById<EditText>(R.id.medicineNameEditText).text.toString()
                val quantity = view.findViewById<EditText>(R.id.quantityEditText).text.toString()
                val time = view.findViewById<EditText>(R.id.timeEditText).text.toString()

                medicineInfo = "Лекарство: $medicineName, Количество: $quantity, Время: $time"
                sendMessageToChat(medicineInfo)



                val patientToken = "euDM1OuXTm6HsdAAw2jifN:APA91bFTHOsWOswRR3y15XEw671RI3AFgvYqPkzF5muSoAW9BT93i4lRag8IwFyURg6nZWSIq0iepfSjzP5C-kbONaOYSA3dhdGv4yHMj53w8b0d1AX3dvsRvv05zMqegMJG5K4F4OYf"
                sendNotificationToPatient(patientToken, medicineInfo)
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }


        return builder.create()
    }

    private fun sendNotificationToPatient(token: String, message: String) {
        val notification = RemoteMessage.Builder(token)
            .setData(mapOf(
                "title" to "Новая информация о лекарстве",
                "body" to message
            ))
            .build()

        FirebaseMessaging.getInstance().send(notification)
        Log.d(TAG, "Уведомление успешно отправлено")
    }


    private fun sendMessageToChat(message: String) {
        try {
            if (::request.isInitialized) {
                val currentUser = currentUserViewModel.getUser().value
                if (currentUser != null) {

                    val newMessage = Message(message, currentUser.id)
                    request.messages.add(newMessage)
                    closeKeyboard()
                    FireStoreDatabase().insertRequest(request, requireView(), this)


                    currentUser.fcmToken?.let { sendFCMMessage(it, message) }

                } else {
                    errorHandled("Error sending message: Current user is null.", requireView())
                }
            } else {
                Log.e(TAG, "Error sending message: Request has not been initialized")
                errorHandled("Error sending message: Request has not been initialized", requireView())
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error sending message: ${ex.message}", ex)
            errorHandled("Error sending message: ${ex.message}", requireView())
        }
    }

    private fun sendFCMMessage(patientToken: String, message: String) {
        FirebaseMessaging.getInstance().send(RemoteMessage.Builder(patientToken)
            .setMessageId((Math.random() * 100).toInt().toString())
            .setData(mapOf("medicine_info" to message))
            .build())
    }

    private fun closeKeyboard() {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage, requireView())
    }
}