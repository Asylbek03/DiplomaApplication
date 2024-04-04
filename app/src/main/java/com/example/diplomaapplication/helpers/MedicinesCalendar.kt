package com.example.diplomaapplication.helpers

import com.example.diplomaapplication.model.CalendarDay
import java.util.*
import kotlin.collections.ArrayList

class MedicinesCalendar {
    private val listOfDaysLetters: ArrayList<String> =
        arrayListOf<String>("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")

    fun getListOfDays(month: Int, year: Int): ArrayList<CalendarDay> {
        val listOfDays: ArrayList<CalendarDay> = arrayListOf<CalendarDay>()
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
        }
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..lastDayOfMonth) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            listOfDays.add(CalendarDay(i, month, year, listOfDaysLetters[dayOfWeek - 1], false))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return listOfDays
    }
    fun getListOfDays(startMonth: Int, startYear: Int, endMonth: Int, endYear: Int): ArrayList<CalendarDay> {
        val listOfDays: ArrayList<CalendarDay> = arrayListOf<CalendarDay>()

        var calendar: Calendar = Calendar.getInstance()

        for (year in startYear..endYear) {
            val startMonthInYear = if (year == startYear) startMonth else Calendar.JANUARY
            val endMonthInYear = if (year == endYear) endMonth else Calendar.DECEMBER

            for (month in startMonthInYear..endMonthInYear) {
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }

                val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                for (i in 1..lastDayOfMonth) {
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    listOfDays.add(CalendarDay(i, month, year, listOfDaysLetters[dayOfWeek - 1], false))
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }
        return listOfDays
    }

    fun getListOfMonths(year: Int): ArrayList<ArrayList<CalendarDay>> {
        val listOfMonths: ArrayList<ArrayList<CalendarDay>> = arrayListOf()
        for (month in Calendar.JANUARY..Calendar.DECEMBER) {
            val listOfDays: ArrayList<CalendarDay> = arrayListOf()
            val calendar: Calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
            }
            val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 1..lastDayOfMonth) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                listOfDays.add(CalendarDay(i, month, year, listOfDaysLetters[dayOfWeek - 1], false))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            listOfMonths.add(listOfDays)
        }
        return listOfMonths
    }


    fun getFirstDay(): CalendarDay {
        val calendar: Calendar = Calendar.getInstance()
        return CalendarDay(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR),
            listOfDaysLetters[calendar.get(
                Calendar.DAY_OF_WEEK
            ) - 1],
            false
        )
    }

}