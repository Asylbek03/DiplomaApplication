package com.example.diplomaapplication.views.medicines

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine

class DeleteMedicineDialog(private val medicine: Medicine, private val listener:DeleteMedicineInterface) : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).setMessage("Вы хотите удалить данное лекарство?")
                .setPositiveButton("Удалить"
                ) { dialog, _ ->
                    listener.deleteMedicine(medicine)
                    dialog.cancel()
                }
                .setNegativeButton("Отмена"
                ) { dialog, _ ->
                    dialog.cancel()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}