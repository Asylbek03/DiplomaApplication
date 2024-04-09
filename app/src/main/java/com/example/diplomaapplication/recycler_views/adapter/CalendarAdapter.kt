package com.example.diplomaapplication.recycler_views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.helpers.MedicinesCalendar
import com.example.diplomaapplication.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarAdapter(private val calendarDays: List<CalendarDay>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    private var onItemClickListener: ((CalendarDay) -> Unit)? = null
    private lateinit var clickedDay: CalendarDay
    val calendar = Calendar.getInstance()

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.dayText)
        val calendarLayout: LinearLayout = itemView.findViewById(R.id.calendarLayout)
        val dayLetterText: TextView = itemView.findViewById(R.id.dayLetterText)
        val monthTextView: TextView = itemView.findViewById(R.id.monthTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendarDay = calendarDays[position]
        holder.dayText.text = calendarDay.day.toString()
        holder.dayLetterText.text = calendarDay.dayLetter
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, calendarDay.month)
        val isFirstInMonth = position == 0 || calendarDays[position - 1].month != calendarDay.month

        if (isFirstInMonth) {
            val currentMonthName = SimpleDateFormat("LLLL", Locale("ru", "KZ")).format(calendar.time)
            holder.monthTextView.text = currentMonthName
            holder.monthTextView.visibility = View.VISIBLE
        } else {
            holder.monthTextView.visibility = View.GONE
        }

        val context = holder.itemView.context
        val textColor = if (calendarDay.isChoose) R.color.white else R.color.black
        val backgroundColor = if (calendarDay.isChoose) R.drawable.rounded20_bg_blue else R.color.white
        holder.dayText.setTextColor(ContextCompat.getColor(context, textColor))
        holder.dayText.setBackgroundResource(backgroundColor)
        holder.dayLetterText.setTextColor(ContextCompat.getColor(context, textColor))
        holder.calendarLayout.setBackgroundResource(backgroundColor)

        holder.dayText.setOnClickListener {
            calendarDays.forEach { it.isChoose = false }
            calendarDay.isChoose = true
            clickedDay = MedicinesCalendar().getFirstDay()
            onItemClickListener?.invoke(calendarDay)
            notifyDataSetChanged()
        }

    }

    fun setOnItemClickListener(listener: (CalendarDay) -> Unit) {
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return calendarDays.size
    }
}
