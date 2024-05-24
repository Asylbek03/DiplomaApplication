package com.example.diplomaapplication.views.medicines

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicineDao
import com.example.diplomaapplication.databases.room_database.medicines_database.MedicinesViewModel
import com.example.diplomaapplication.databinding.FragmentAddMedicineBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.notification.MedicineAlarmReceiver
import com.example.diplomaapplication.model.MedicineFormCard
import com.example.diplomaapplication.model.views.DateTimePickerViewModel
import com.example.diplomaapplication.recycler_views.adapter.MedicineFormsRecyclerViewAdapter
import com.example.diplomaapplication.views.DatePickerHelper
import com.example.diplomaapplication.views.TimePickerHelper
import java.lang.Exception
import java.util.*

class AddMedicineFragment : Fragment(), MedicineFormInterface {

    private lateinit var medicine: Medicine
    private lateinit var medicinesViewModel: MedicinesViewModel
    private lateinit var alarmManager: AlarmManager
    private lateinit var medicineDao: MedicineDao
    private var _binding: FragmentAddMedicineBinding? = null
    private val binding get() = _binding!!
    private var requestCodeCounter = 0
    private lateinit var intakeTimingSpinner: Spinner
    private lateinit var customDescriptionEditText: EditText
    private lateinit var intakeTimingAdapter: ArrayAdapter<String>
    private val units = arrayOf("таблетки", "мл", "мг")
    private var selectedMedicineType: String = ""

    private val pendingIntentsMap = HashMap<Int, PendingIntent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        intakeTimingSpinner = view.findViewById(R.id.intakeTimingSpinner)
        customDescriptionEditText = view.findViewById(R.id.customDescriptionEditText)

        val intakeTimingOptions = resources.getStringArray(R.array.intake_timing_options)
        intakeTimingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, intakeTimingOptions)
        intakeTimingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        intakeTimingSpinner.adapter = intakeTimingAdapter

        intakeTimingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTiming = intakeTimingOptions[position]
                customDescriptionEditText.setText("Время потребление: $selectedTiming")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("ItemSelected", "No timing selected")
            }
        }

        medicinesViewModel = ViewModelProvider(this).get(MedicinesViewModel::class.java)
        medicine = Medicine(""," "," ",Calendar.getInstance().timeInMillis,1,"",false ," "," ", R.drawable.icon_pills)

        setupRecyclerView()
        setupMedicineType()
        setupDateAndTimePicker()

        binding.saveMedicineButton.setOnClickListener {
            insertMedicine()
            Log.d("MedicineAdd", "Updating medicine ${medicine.id} ")
        }

        binding.addMedicineBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.cameraButton.setOnClickListener {
            openCameraFragment()
        }

        val durationPicker = binding.durationPicker
        val unitSpinner = binding.unitSpinner
        durationPicker.minValue = 1
        durationPicker.maxValue = 31
        durationPicker.value = 1
        val units = arrayOf("день", "неделя", "месяц")
        unitSpinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner_list, units)
    }

    private fun setupRecyclerView() {
        val listOfMedicinesForms = arrayListOf<MedicineFormCard>(
            MedicineFormCard("Таблетка", R.drawable.icon_pills, true),
            MedicineFormCard("Капсула", R.drawable.icon_capsule, false),
            MedicineFormCard("Сироп", R.drawable.icon_syrup, false),
            MedicineFormCard("Крем", R.drawable.icon_cream, false),
            MedicineFormCard("Укол", R.drawable.icon_syringe, false)
        )

        binding.medicineFormRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.medicineFormRecyclerView.adapter = MedicineFormsRecyclerViewAdapter(listOfMedicinesForms, this)
    }

    override fun changeForm(form: MedicineFormCard) {
        medicine.formName = form.title
        medicine.formImage = form.photo
    }

    private fun setupMedicineType() {
        binding.medicineTypeChooser.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayListOf("таблетки", "ml", "mg")))
        binding.medicineTypeChooser.inputType = 0
        binding.medicineTypeChooser.keyListener = null
        binding.medicineTypeChooser.setOnClickListener {
            binding.medicineTypeChooser.showDropDown()
        }

        binding.medicineTypeChooser.setOnItemClickListener { _, _, i, _ ->
            selectedMedicineType = units[i]
        }

        val helper: Helpers = Helpers()
        helper.keyboardEnterButtonClick(binding.amountInputField) { closeKeyboard() }
    }

    private fun closeKeyboard() {
        val imm: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun setupDateAndTimePicker() {
        val dateTimePickerViewModel: DateTimePickerViewModel = ViewModelProvider(requireActivity()).get(DateTimePickerViewModel::class.java)
        val c = Calendar.getInstance()
        dateTimePickerViewModel.setDate(c.timeInMillis)
        dateTimePickerViewModel.setTime(c.timeInMillis)

        binding.chooseTimeButton.setOnClickListener {
            val timePickerDialog: TimePickerHelper = TimePickerHelper()
            timePickerDialog.show(requireActivity().supportFragmentManager, "time_picker")
        }
        binding.timeTextInput.setOnClickListener {
            val timePickerDialog: TimePickerHelper = TimePickerHelper()
            timePickerDialog.show(requireActivity().supportFragmentManager, "time_picker")
        }

        binding.chooseDateButton.setOnClickListener {
            val datePickerDialog: DatePickerHelper = DatePickerHelper()
            datePickerDialog.show(requireActivity().supportFragmentManager, "date_picker")
        }
        binding.dateTextInput.setOnClickListener {
            val datePickerDialog: DatePickerHelper = DatePickerHelper()
            datePickerDialog.show(requireActivity().supportFragmentManager, "date_picker")
        }

        dateTimePickerViewModel.getTime().observe(viewLifecycleOwner, Observer { time ->
            val helper = Calendar.getInstance()
            helper.timeInMillis = time
            c.set(Calendar.HOUR_OF_DAY, helper.get(Calendar.HOUR_OF_DAY))
            c.set(Calendar.MINUTE, helper.get(Calendar.MINUTE))
            c.set(Calendar.SECOND, helper.get(Calendar.SECOND))
            medicine.time = c.timeInMillis
            binding.timeTextInput.text = DateFormat.format("HH:mm", helper).toString()
        })

        dateTimePickerViewModel.getDate().observe(viewLifecycleOwner, Observer { date ->
            val helper = Calendar.getInstance()
            helper.timeInMillis = date
            c.set(Calendar.YEAR, helper.get(Calendar.YEAR))
            c.set(Calendar.MONTH, helper.get(Calendar.MONTH))
            c.set(Calendar.DAY_OF_MONTH, helper.get(Calendar.DAY_OF_MONTH))
            medicine.time = c.timeInMillis
            binding.dateTextInput.text = DateFormat.format("dd MMMM yyyy", helper).toString()
        })
    }

    private fun insertMedicine() {
        val helpers: Helpers = Helpers()
        medicine.name = if (binding.medicineNameInput.text.isNullOrEmpty()) "Medicine" else binding.medicineNameInput.text.toString()
        medicine.amount = if (binding.amountInputField.text.isNullOrEmpty()) "Blank" else binding.amountInputField.text.toString()

        try {
            if (medicine.time + 100000 > System.currentTimeMillis()) {
                helpers.showSnackBar("Сохранено", requireView())
                val c = Calendar.getInstance()
                c.timeInMillis = medicine.time

                val duration = binding.durationPicker.value
                val durationUnit = binding.unitSpinner.selectedItemPosition
                val durationUnitStr = binding.unitSpinner.selectedItem.toString()

                medicine.isTaken = false

                val selectedTimingPosition = intakeTimingSpinner.selectedItemPosition
                val selectedTiming = intakeTimingAdapter.getItem(selectedTimingPosition)
                


                medicine.type = selectedMedicineType

                val customDescription = customDescriptionEditText.text.toString()
                medicine.description = customDescription


                val totalDurationInDays = when (durationUnit) {
                    0 -> duration
                    1 -> duration * 7
                    2 -> duration * 30
                    else -> duration
                }

                for (i in 1..totalDurationInDays) {


                    val medicineToSave = Medicine(medicine.name, medicine.amount, medicine.type, c.timeInMillis, medicine.duration,
                        durationUnitStr.lowercase(Locale.getDefault()), medicine.isTaken, medicine.description,
                        medicine.formName, medicine.formImage)
                    medicinesViewModel.insertMedicine(medicineToSave)
                    val intent = Intent(requireActivity().applicationContext, MedicineAlarmReceiver::class.java)
                    intent.apply {
                        putExtra("medicineName", medicine.name)
                        putExtra("medicineAmount", medicine.amount)
                        putExtra("medicineType", medicine.type)
                        putExtra("medicineImage", medicine.formImage)
                    }

                    val result = Bundle().apply {
                        putString("medicineName", medicine.name)
                        putString("medicineAmount", medicine.amount)
                        putString("medicineType", medicine.type)
                        putLong("medicineTime", medicine.time)
                        putInt("medicineDuration", medicine.duration)
                        putString("medicineDurationUnit", medicine.durationUnit)
                        putBoolean("medicineIsTaken", medicine.isTaken)
                        putString("medicineDescription", medicine.description)
                        putString("medicineFormName", medicine.formName)
                        putInt("medicineImage", medicine.formImage)
                        putInt("medicineId", medicine.id)
                    }
                    setFragmentResult("addMedicineResult", result)

                    val alarmId = System.currentTimeMillis().toInt()

                    cancelExistingAlarm(alarmManager, intent, alarmId)

                    val alarmIntent = PendingIntent.getBroadcast(requireActivity().applicationContext, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)

                    alarmManager.set(AlarmManager.RTC_WAKEUP, medicineToSave.time, alarmIntent)

                    pendingIntentsMap[alarmId] = alarmIntent

                    c.add(Calendar.DAY_OF_MONTH, 1)
                }

                requireActivity().onBackPressed()
            } else {
                helpers.showSnackBar("Невозможно сохранить лекарство, дата которого уже прошла.", requireView())
            }
        } catch (ex: Exception) {
            Log.d("TAG", ex.toString())
            helpers.showSnackBar(ex.message.toString(), requireView())
        }
    }
    private fun getNextRequestCode(): Int {
        return requestCodeCounter++
    }
    private fun cancelExistingAlarm(alarmManager: AlarmManager, intent: Intent, alarmId: Int) {
        pendingIntentsMap[alarmId]?.let {
            alarmManager.cancel(it)
            pendingIntentsMap.remove(alarmId)
        }
    }

    private fun openCameraFragment() {
        val intent = Intent(requireContext(), CameraFragment::class.java)
        startActivity(intent)
    }

    fun updateMedicineName(medicineName: String) {
        binding.medicineNameInput.setText(medicineName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
