package com.example.diplomaapplication.views.doctor

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.diplomaapplication.model.Request

class ConfirmDialog(private val title: String, private val message: String, private val isDoctor:Boolean, private val request: Request, private val listener: ChatInterface) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).setTitle(title).setMessage(message)
                .setPositiveButton(
                    "Да"
                ) { dialog, _ ->
                    if(isDoctor) listener.onDoctorLeave(request)

                    else listener.onDeleteChat(request.id)
                    dialog.cancel()
                }
                .setNegativeButton(
                    "Отмена"
                ) { dialog, _ ->
                    dialog.cancel()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}