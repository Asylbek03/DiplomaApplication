package com.example.diplomaapplication.views.medicines

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.diplomaapplication.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("ViewConstructor")
class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null && e is BarEntry) {
            val time = getTimeForEntry(e)
            val taken = e.y.toInt()
            tvContent.text = "Выпито: $taken\n"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    private fun getTimeForEntry(e: Entry): String {
        val startTimeMillis = e.x.toLong()
        val endTimeMillis = startTimeMillis + TimeUnit.HOURS.toMillis(1)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = dateFormat.format(startTimeMillis)
        val endTime = dateFormat.format(endTimeMillis)

        return "$startTime - $endTime"
    }

}
