package com.example.diplomaapplication.databases.room_database.medicines_database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicinesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicinesRepository = MedicinesRepository(application, MedicinesDatabase.getDatabase(application)
        .medicineDao())
    val allMedicines: LiveData<List<Medicine>> = repository.allMedicines

    // Define newMedicine as MutableLiveData
    private val _newMedicine = MutableLiveData<Medicine>()
    val newMedicine: LiveData<Medicine>
        get() = _newMedicine

    fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMedicine(medicine)
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMedicine(medicine)
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedicine(medicine)
        }
    }

    fun getMedicinesInTimeRange(startTime: Long, endTime: Long): LiveData<List<Medicine>> {
        return repository.getMedicinesInTimeRange(startTime, endTime)
    }

    fun updateMedicineIsTaken(medicineId: Int, isTaken: Boolean) {
        viewModelScope.launch {
            repository.updateMedicineIsTaken(medicineId, isTaken)
        }
    }

    fun updateMedicineTakenStatus(medicineId: Int, isTaken: Boolean) {
        viewModelScope.launch {
            repository.updateMedicineTakenStatus(medicineId, isTaken)
        }
    }



    fun getMedicinesForPeriod(startTime: Long, endTime: Long): LiveData<List<Medicine>> {
        return repository.getMedicinesForPeriod(startTime, endTime)
    }

    // Set the value of newMedicine
    fun setNewMedicine(medicine: Medicine) {
        _newMedicine.value = medicine
    }
}

