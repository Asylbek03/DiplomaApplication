package com.example.diplomaapplication.databases.medicines_database

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MedicinesRepository(application: Application) {

    private val medicinesDao: MedicineDao

    init {
        val database = MedicinesDatabase.getDatabase(application.applicationContext)
        this.medicinesDao = database!!.medicineDao()
    }

    suspend fun insertMedicine(medicine: Medicine){
        medicinesDao.insertMedicine(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine){
        medicinesDao.updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine){
        medicinesDao.deleteMedicine(medicine)
    }

    fun getMedicinesForPeriod(startDate: Long, endDate: Long): LiveData<List<Medicine>> {
        return medicinesDao.getMedicinesForPeriod(startDate, endDate)
    }

    fun getMedicinesInTimeRange(startDate: Long, endDate: Long): LiveData<List<Medicine>> {
        return medicinesDao.getMedicinesForPeriod(startDate, endDate)
    }
    val allMedicines = medicinesDao.getAllMedicines()

}