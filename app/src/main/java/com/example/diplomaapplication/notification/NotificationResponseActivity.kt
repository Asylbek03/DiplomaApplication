package com.example.diplomaapplication.notification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesDatabase
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesViewModel
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesRepository


class NotificationResponseActivity : AppCompatActivity() {
    private lateinit var medicinesViewModel: MedicinesViewModel
    private lateinit var repository: MedicinesRepository

    private var medicineId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_response)

        val medicineName = intent?.getStringExtra("medicineName")

        val context: Context = applicationContext

        val medicineDao = MedicinesDatabase.getDatabase(application).medicineDao()

        repository = MedicinesRepository(context, medicineDao)

        medicinesViewModel = ViewModelProvider(this).get(MedicinesViewModel::class.java)

        medicineId = intent.getIntExtra("medicineId", -1)
        Log.d("NotificationResponseActivity", "Intent extras: ${intent.extras}")
        Log.d("NotificationResponseActivity", "Medicine ID received: $medicineId")
        findViewById<Button>(R.id.yesButton).setOnClickListener {
            if (medicineId != -1) {
                if (medicineId != null) {
                    updateMedicineIsTaken(medicineId, true)
                }
            }
            finish()
        }

        findViewById<Button>(R.id.noButton).setOnClickListener {
            if (medicineId != -1) {
                if (medicineId != null) {
                    updateMedicineIsTaken(medicineId, false)
                }
            }
            finish()
        }

        // Launch coroutine to get medicine id
//        CoroutineScope(Dispatchers.Main).launch {
//            //val medicineId = getMedicineIdByName(medicineName)
//
//            Log.d("NotificationResponseActivity", "Intent extras: ${intent.extras}")
//            Log.d("NotificationResponseActivity", "Medicine ID received: $medicineId")
//            findViewById<Button>(R.id.yesButton).setOnClickListener {
//                if (medicineId != -1) {
//                    if (medicineId != null) {
//                        updateMedicineIsTaken(medicineId, true)
//                    }
//                }
//                finish()
//            }
//
//            findViewById<Button>(R.id.noButton).setOnClickListener {
//                if (medicineId != -1) {
//                    if (medicineId != null) {
//                        updateMedicineIsTaken(medicineId, false)
//                    }
//                }
//                finish()
//            }
//        }
    }
    override fun onResume() {
        super.onResume()

        medicineId = intent.getIntExtra("medicineId", -1)
    }

    private fun updateMedicineIsTaken(medicineId: Int, isTaken: Boolean) {
        // Используйте medicinesViewModel, только если он был инициализирован
        if (::medicinesViewModel.isInitialized) {
            medicinesViewModel.updateMedicineTakenStatus(medicineId, isTaken)
        } else {
            Log.e("NotificationResponseActivity", "MedicinesViewModel is not initialized.")
        }
    }

    private suspend fun getMedicineIdByName(medicineName: String?): Int? {
        return if (medicineName != null) {
            repository.getMedicineIdByName(medicineName)
        } else {
            -1
        }
    }
}
