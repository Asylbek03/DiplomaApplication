package com.example.diplomaapplication.views.medicines

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesViewModel
import com.example.diplomaapplication.databinding.FragmentAddMedicineBinding
import com.example.diplomaapplication.databinding.FragmentRegisterBinding
import com.example.diplomaapplication.databinding.FragmentShowMedicinesStatisticBinding
import com.example.diplomaapplication.model.views.RegisterTimeViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class ShowMedicinesStatistic : Fragment() {

    private lateinit var medicinesViewModel: MedicinesViewModel
    private lateinit var barChart: BarChart
    private lateinit var customMarkerView: CustomMarkerView
    private var _binding: FragmentShowMedicinesStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowMedicinesStatisticBinding.inflate(inflater, container, false)
        val root = binding.root
        barChart = root.findViewById(R.id.bar_chart)

        medicinesViewModel = ViewModelProvider(this).get(MedicinesViewModel::class.java)

        customMarkerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)



        root.findViewById<Button>(R.id.btn_day).setOnClickListener {
            showMedicinesStatisticForPeriod(getStartTimeForPeriod(Calendar.DAY_OF_YEAR), getCurrentTime(), Calendar.DAY_OF_YEAR)
        }

        root.findViewById<Button>(R.id.btn_week).setOnClickListener {
            showMedicinesStatisticForPeriod(getStartTimeForPeriod(Calendar.WEEK_OF_YEAR), getCurrentTime(), Calendar.WEEK_OF_YEAR)
        }

        root.findViewById<Button>(R.id.btn_month).setOnClickListener {
            showMedicinesStatisticForPeriod(getStartTimeForPeriod(Calendar.MONTH), getCurrentTime(), Calendar.MONTH)
        }

        root.findViewById<Button>(R.id.btn_year).setOnClickListener {
            showMedicinesStatisticForPeriod(getStartTimeForPeriod(Calendar.YEAR), getCurrentTime(), Calendar.YEAR)
        }

        root.findViewById<ImageView>(R.id.showStatisticBackButton).setOnClickListener {
            requireActivity().onBackPressed()
        }

        val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
        barChart.marker = markerView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMedicinesStatisticForPeriod(getStartTimeForPeriod(Calendar.DAY_OF_YEAR), getCurrentTime(), Calendar.DAY_OF_YEAR)
    }

    @SuppressLint("SetTextI18n")
    private fun showMedicinesStatisticForPeriod(startTime: Long, endTime: Long, periodType: Int) {
        medicinesViewModel.getMedicinesForPeriod(startTime, endTime).observe(viewLifecycleOwner) { medicines ->
            val entries = ArrayList<BarEntry>()

            val totalTakenMedicines = getTotalTakenMedicines(medicines)
            binding.overallTablets.text = "Выпито: $totalTakenMedicines"

            val xAxisLabels = getXAxisLabels(startTime, endTime, periodType)

            for (i in xAxisLabels.indices) {
                val timePeriodStart = getTimePeriodStart(startTime, periodType, i)
                val timePeriodEnd = getTimePeriodEnd(startTime, periodType, i)
                val takenCount =
                    medicines.count { it.isTaken && it.time >= timePeriodStart && it.time < timePeriodEnd }
                entries.add(BarEntry(i.toFloat(), takenCount.toFloat()))
            }

            val dataSet = BarDataSet(entries, "Medicines Taken")
            dataSet.color = Color.parseColor("#4CAF50")

            val data = BarData(dataSet)

            barChart.data = data
            barChart.xAxis.valueFormatter =
                IndexAxisValueFormatter(xAxisLabels)

            barChart.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            barChart.xAxis.labelRotationAngle = 0f
            barChart.xAxis.granularity = 1f

            barChart.legend.isEnabled = false

            barChart.description.text = "Принятые лекарства"
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.xAxis.textSize = 12f
            barChart.axisLeft.textSize = 12f

            barChart.animateY(1000, Easing.EaseInOutCubic)

            barChart.invalidate()
        }
    }

    private fun getTimePeriodStart(startTime: Long, periodType: Int, index: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime
        when (periodType) {
            Calendar.DAY_OF_YEAR -> calendar.add(Calendar.HOUR_OF_DAY, index)
            Calendar.WEEK_OF_YEAR -> calendar.add(Calendar.DAY_OF_YEAR, index)
            Calendar.MONTH -> calendar.add(Calendar.WEEK_OF_MONTH, index)
            Calendar.YEAR -> calendar.add(Calendar.MONTH, index)
        }
        return calendar.timeInMillis
    }

    private fun getTimePeriodEnd(startTime: Long, periodType: Int, index: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime
        when (periodType) {
            Calendar.DAY_OF_YEAR -> calendar.add(Calendar.HOUR_OF_DAY, index + 1)
            Calendar.WEEK_OF_YEAR -> calendar.add(Calendar.DAY_OF_YEAR, index + 1)
            Calendar.MONTH -> calendar.add(Calendar.WEEK_OF_MONTH, index + 1)
            Calendar.YEAR -> calendar.add(Calendar.MONTH, index + 1)
        }
        return calendar.timeInMillis
    }

    private fun getTotalTakenMedicines(medicines: List<Medicine>): Int {
        return medicines.count { it.isTaken }
    }

    private fun getStartTimeForPeriod(period: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        when (period) {
            Calendar.WEEK_OF_YEAR -> calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            Calendar.MONTH -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            Calendar.YEAR -> calendar.set(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    private fun getXAxisLabels(startTime: Long, endTime: Long, periodType: Int): List<String> {
        val labels = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime

        var currentWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

        while (calendar.timeInMillis <= endTime) {
            val label: String = when (periodType) {
                Calendar.DAY_OF_YEAR -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                Calendar.WEEK_OF_YEAR -> SimpleDateFormat("dd.MM", Locale.getDefault()).format(calendar.time)
                Calendar.MONTH -> {
                    if (currentWeekOfMonth != calendar.get(Calendar.WEEK_OF_MONTH)) {
                        currentWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
                        "Неделя $currentWeekOfMonth"
                    } else {
                        ""
                    }
                }
                Calendar.YEAR -> SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                else -> ""
            }
            labels.add(label)
            when (periodType) {
                Calendar.DAY_OF_YEAR -> calendar.add(Calendar.HOUR_OF_DAY, 1)
                Calendar.WEEK_OF_YEAR -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                Calendar.MONTH -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                Calendar.YEAR -> calendar.add(Calendar.MONTH, 1)
            }
        }

        return labels
    }

}

