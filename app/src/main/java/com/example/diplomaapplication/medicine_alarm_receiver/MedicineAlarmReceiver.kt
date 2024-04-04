package com.example.diplomaapplication.medicine_alarm_receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.diplomaapplication.MainActivity
import com.example.diplomaapplication.MainPage
import com.example.diplomaapplication.R


class MedicineAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val MEDICATION_INTENT = "medication_id"
        const val MEDICATION_NOTIFICATION = "medication_notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val activityPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainPage::class.java),
            PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = NotificationCompat.Builder(context, MEDICATION_INTENT)
            .setContentTitle(intent!!.getStringExtra("medicineName"))
            .setContentText(intent.getStringExtra("medicineAmount") + " "+ intent.getStringExtra("medicineType"))
            .setSmallIcon(intent.getIntExtra("medicineImage", R.drawable.capsule))
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(MEDICATION_INTENT, MEDICATION_NOTIFICATION, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(),notification)

    }
}