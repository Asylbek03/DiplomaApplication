package com.example.diplomaapplication.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.diplomaapplication.MainActivity
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.medicines_database.MedicinesDatabase
import com.example.diplomaapplication.databases.medicines_database.MedicinesRepository
import kotlinx.coroutines.runBlocking


class MedicineAlarmReceiver : BroadcastReceiver() {
    private lateinit var repository: MedicinesRepository
    companion object {
        const val MEDICATION_INTENT = "medication_id"
        const val MEDICATION_NOTIFICATION = "medication_notification"
        const val ACTION_YES = "action_yes"
        const val ACTION_NO = "action_no"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val database = MedicinesDatabase.getDatabase(context!!.applicationContext)
        repository = MedicinesRepository(context.applicationContext, database.medicineDao())

        val action = intent?.action
        val medicineName = intent?.getStringExtra("medicineName")

        runBlocking {
            val medicineId = getMedicineIdByName(medicineName)
            Log.d("MedicineAlarmReceiver", "Medicine ID received: $medicineId")

            val yesPendingIntent = intent?.let {
                if (medicineId != null) {
                    createYesPendingIntent(context, it, medicineId)
                } else {
                    null
                }
            }

            val noPendingIntent = intent?.let {
                if (medicineId != null) {
                    createNoPendingIntent(context, it, medicineId)
                } else {
                    null
                }
            }


            if (action == ACTION_YES) {
                if (medicineId != null) {
                    repository.updateMedicineTakenStatus(medicineId, true)
                }
            } else if (action == ACTION_NO) {
                if (medicineId != null) {
                    repository.updateMedicineTakenStatus(medicineId, false)
                }
            } else {
                val activityPendingIntent = PendingIntent.getActivity(
                    context, 0, Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notification: Notification =
                    NotificationCompat.Builder(context, MEDICATION_INTENT)
                        .setContentTitle(intent!!.getStringExtra("medicineName"))
                        .setContentText(
                            intent.getStringExtra("medicineAmount") + " " + intent.getStringExtra(
                                "medicineType"
                            )
                        )
                        .setSmallIcon(intent.getIntExtra("medicineImage", R.drawable.icon_capsule))
                        .setContentIntent(activityPendingIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_yes, "Да", yesPendingIntent)
                        .addAction(R.drawable.ic_no, "Нет", noPendingIntent)
                        .build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        MEDICATION_INTENT,
                        MEDICATION_NOTIFICATION,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(System.currentTimeMillis().toInt(), notification)

            }
        }
    }

    private fun createYesPendingIntent(context: Context, intent: Intent, requestCode: Int): PendingIntent? {
        val yesIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = ACTION_YES
            putExtra("medicineName", intent.getStringExtra("medicineName"))
            putExtra("medicineId", intent.getIntExtra("medicineId", -1))
        }

        return PendingIntent.getBroadcast(context, requestCode, yesIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNoPendingIntent(context: Context, intent: Intent, requestCode: Int): PendingIntent? {
        val noIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = ACTION_NO
            putExtra("medicineName", intent.getStringExtra("medicineName"))
            putExtra("medicineId", intent.getIntExtra("medicineId", -1))
        }

        return PendingIntent.getBroadcast(context, requestCode, noIntent, PendingIntent.FLAG_IMMUTABLE)
    }






    private suspend fun getMedicineIdByName(medicineName: String?): Int? {
        return if (medicineName != null) {
            repository.getMedicineIdByName(medicineName)
        } else {
            -1
        }
    }
}
