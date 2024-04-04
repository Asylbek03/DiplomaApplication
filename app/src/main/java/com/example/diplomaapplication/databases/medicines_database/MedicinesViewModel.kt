package com.example.diplomaapplication.databases.medicines_database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MedicinesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicinesRepository = MedicinesRepository(application)
    val allMedicines: LiveData<List<Medicine>> = repository.allMedicines

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

    fun getMedicinesForPeriod(startTime: Long, endTime: Long): LiveData<List<Medicine>> {
        return repository.getMedicinesForPeriod(startTime, endTime)
    }
}