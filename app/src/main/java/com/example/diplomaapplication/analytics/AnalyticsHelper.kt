package com.example.diplomaapplication.analytics

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine
import com.google.firebase.analytics.FirebaseAnalytics

import java.util.Date

private const val MEDICATION_TIME = "medication_time"
private const val MEDICATION_END_DATE = "medication_end_date"
private const val NOTIFICATION_TIME = "notification_time"

class AnalyticsHelper(
    context: Context
) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun trackNotificationShown(medicine: Medicine) {
        val params = bundleOf(
            MEDICATION_TIME to medicine.time.toString(),
            MEDICATION_END_DATE to medicine.duration.toString(),
            //NOTIFICATION_TIME to Date().toFormattedDateString()
        )
        logEvent(AnalyticsEvents.MEDICATION_NOTIFICATION_SHOWN, params)
    }

    fun trackNotificationScheduled(medicine: Medicine) {
        val params = bundleOf(
            MEDICATION_TIME to medicine.time.toString(),
            MEDICATION_END_DATE to medicine.duration.toString(),
            //NOTIFICATION_TIME to Date().toFormattedDateString()
        )
        logEvent(AnalyticsEvents.MEDICATION_NOTIFICATION_SCHEDULED, params)
    }

    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }
}
