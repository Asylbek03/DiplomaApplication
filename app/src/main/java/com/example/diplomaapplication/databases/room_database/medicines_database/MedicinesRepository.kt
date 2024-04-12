package com.example.diplomaapplication.databases.room_database.medicines_database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicinesRepository(private val context: Context, private val medicineDao: MedicineDao) {

    private val medicinesDao: MedicineDao

    init {
        val database = MedicinesDatabase.getDatabase(context)
        this.medicinesDao = database.medicineDao()
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

    suspend fun updateIsTaken(medicineId: Int, isTaken: Boolean) {
        withContext(Dispatchers.IO) {
            medicinesDao.updateIsTaken(medicineId, isTaken)
        }
    }

    suspend fun getMedicineIdByName(medicineName: String): Int? {
        return medicinesDao.getMedicineIdByName(medicineName)
    }

    @WorkerThread
    suspend fun updateMedicineIsTaken(medicineId: Int, isTaken: Boolean) {
        withContext(Dispatchers.IO) {
            medicinesDao.updateMedicineIsTaken(medicineId, isTaken)
        }
    }
    val allMedicines = medicinesDao.getAllMedicines()



    suspend fun updateMedicineTakenStatus(medicineId: Int, isTaken: Boolean) {
        withContext(Dispatchers.IO) {
            medicinesDao.updateMedicineTakenStatus(medicineId, isTaken)
        }
    }

}