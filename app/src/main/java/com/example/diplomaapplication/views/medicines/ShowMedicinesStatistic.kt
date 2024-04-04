package com.example.diplomaapplication.views.medicines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.medicines_database.Medicine
import com.example.diplomaapplication.databases.medicines_database.MedicinesViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.JANUARY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.SECOND
import java.util.Calendar.WEEK_OF_YEAR
import java.util.Calendar.YEAR
import kotlin.collections.ArrayList

class ShowMedicinesStatistic : Fragment() {

    private lateinit var medicinesViewModel: MedicinesViewModel
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_medicines_statistic, container, false)

        lineChart = view.findViewById(R.id.lineChart)
        medicinesViewModel = ViewModelProvider(this).get(MedicinesViewModel::class.java)

        view.findViewById<Button>(R.id.buttonDay).setOnClickListener {
            // Загрузка статистики за день
            loadStatisticsForDay()
        }

        view.findViewById<Button>(R.id.buttonWeek).setOnClickListener {
            // Загрузка статистики за неделю
            loadStatisticsForWeek()
        }

        view.findViewById<Button>(R.id.buttonMonth).setOnClickListener {
            // Загрузка статистики за месяц
            loadStatisticsForMonth()
        }

        view.findViewById<Button>(R.id.buttonYear).setOnClickListener {
            // Загрузка статистики за год
            loadStatisticsForYear()
        }

        observeMedicines()

        return view
    }

    private fun observeMedicines() {
        medicinesViewModel.allMedicines.observe(viewLifecycleOwner, Observer { medicines ->
            updateChart(medicines)
        })
    }

    private fun updateChart(medicines: List<Medicine>) {
        // Очистка графика перед обновлением
        lineChart.clear()

        // Логика по добавлению данных на график и настройка графика
        // Пример:
        val entries = ArrayList<Entry>()
        medicines.forEachIndexed { index, medicine ->
            if (medicine.amount != "Blank") {
                val amount = medicine.amount.toFloatOrNull()
                amount?.let {
                    entries.add(Entry(index.toFloat(), it))
                }
            }
        }

        val dataSet = LineDataSet(entries, "Amount of Medicine")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Настройка оси X
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = DateAxisFormatter()

        lineChart.invalidate()
    }


    private fun loadStatisticsForDay() {
        val calendar = Calendar.getInstance()
        calendar.set(HOUR_OF_DAY, 0)
        calendar.set(MINUTE, 0)
        calendar.set(SECOND, 0)
        val startOfDay = calendar.timeInMillis
        calendar.set(HOUR_OF_DAY, 23)
        calendar.set(MINUTE, 59)
        calendar.set(SECOND, 59)
        val endOfDay = calendar.timeInMillis

        // Здесь можно использовать вашу ViewModel для загрузки данных из базы данных
        // Например:
        medicinesViewModel.getMedicinesInTimeRange(startOfDay, endOfDay).observe(viewLifecycleOwner, Observer { medicines ->
            updateChart(medicines)
        })
    }

    private fun loadStatisticsForWeek() {
        val calendar = Calendar.getInstance()
        calendar.set(DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(HOUR_OF_DAY, 0)
        calendar.set(MINUTE, 0)
        calendar.set(SECOND, 0)
        val startOfWeek = calendar.timeInMillis
        calendar.add(WEEK_OF_YEAR, 1)
        calendar.add(MILLISECOND, -1)
        val endOfWeek = calendar.timeInMillis

        // Здесь можно использовать вашу ViewModel для загрузки данных из базы данных
        // Например:
        medicinesViewModel.getMedicinesInTimeRange(startOfWeek, endOfWeek).observe(viewLifecycleOwner, Observer { medicines ->
            updateChart(medicines)
        })
    }

    private fun loadStatisticsForMonth() {
        val calendar = Calendar.getInstance()
        calendar.set(DAY_OF_MONTH, 1)
        calendar.set(HOUR_OF_DAY, 0)
        calendar.set(MINUTE, 0)
        calendar.set(SECOND, 0)
        val startOfMonth = calendar.timeInMillis
        calendar.add(MONTH, 1)
        calendar.add(MILLISECOND, -1)
        val endOfMonth = calendar.timeInMillis

        // Здесь можно использовать вашу ViewModel для загрузки данных из базы данных
        // Например:
        medicinesViewModel.getMedicinesInTimeRange(startOfMonth, endOfMonth).observe(viewLifecycleOwner, Observer { medicines ->
            updateChart(medicines)
        })
    }

    private fun loadStatisticsForYear() {
        val calendar = Calendar.getInstance()
        calendar.set(MONTH, JANUARY)
        calendar.set(DAY_OF_MONTH, 1)
        calendar.set(HOUR_OF_DAY, 0)
        calendar.set(MINUTE, 0)
        calendar.set(SECOND, 0)
        val startOfYear = calendar.timeInMillis
        calendar.add(YEAR, 1)
        calendar.add(MILLISECOND, -1)
        val endOfYear = calendar.timeInMillis

        // Здесь можно использовать вашу ViewModel для загрузки данных из базы данных
        // Например:
        medicinesViewModel.getMedicinesInTimeRange(startOfYear, endOfYear).observe(viewLifecycleOwner, Observer { medicines ->
            updateChart(medicines)
        })
    }

    private inner class DateAxisFormatter : ValueFormatter() {
        private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val millis = value.toLong()
            return dateFormat.format(Date(millis))
        }
    }
}
