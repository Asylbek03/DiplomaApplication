package com.example.diplomaapplication.views.medicines

import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine

interface DeleteMedicineInterface {

    fun showDeleteDialog(medicine: Medicine)

    fun deleteMedicine(medicine: Medicine)

}