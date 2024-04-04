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
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.medicines_database.Medicine
import com.example.diplomaapplication.databases.medicines_database.MedicinesViewModel
import com.example.diplomaapplication.databinding.FragmentAddMedicineBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.medicine_alarm_receiver.MedicineAlarmReceiver
import com.example.diplomaapplication.model.MedicineFormCard
import com.example.diplomaapplication.model.views.DateTimePickerViewModel
import com.example.diplomaapplication.recycler_views.MedicineFormsRecyclerViewAdapter
import com.example.diplomaapplication.ui.medicines.MedicinesFragment
import com.example.diplomaapplication.views.DatePickerHelper
import com.example.diplomaapplication.views.TimePickerHelper
import java.lang.Exception
import java.util.*


class AddMedicineFragment : Fragment(), MedicineFormInterface {

    private lateinit var medicine: Medicine
    private lateinit var medicinesViewModel : MedicinesViewModel
    private lateinit var alarmManager: AlarmManager
    private var _binding: FragmentAddMedicineBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        medicinesViewModel =  ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(MedicinesViewModel::class.java)
        medicine =  Medicine("","3","pills",Calendar.getInstance().timeInMillis,3,"Pills",R.drawable.pills)


        setupRecyclerView()
        //onSeekbarChanged()
        setupMedicineType()
        setupDateAndTimePicker()

        binding.saveMedicineButton.setOnClickListener {
            insertMedicine()
        }

        binding.addMedicineBackButton.setOnClickListener {
            requireActivity().onBackPressed()

//            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main_page, MedicinesFragment())
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
        }

        val durationPicker = binding.durationPicker
        val unitSpinner = binding.unitSpinner
        durationPicker.minValue = 1
        durationPicker.maxValue = 31

        durationPicker.value = 1
        val units = arrayOf("дней", "недель", "месяцев")
        unitSpinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner_list, units)



    }



    private fun setupRecyclerView(){
        val listOfMedicinesForms = arrayListOf<MedicineFormCard>(
            MedicineFormCard("Таблетки",R.drawable.pills,true),
            MedicineFormCard("Капсула",R.drawable.capsule,false),
            MedicineFormCard("Сироп",R.drawable.syrup,false),
            MedicineFormCard("Крем",R.drawable.cream,false),
            MedicineFormCard("Drops",R.drawable.drops,false),
            MedicineFormCard("Укол",R.drawable.syringe,false)
        )

        binding.medicineFormRecyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding.medicineFormRecyclerView.adapter = MedicineFormsRecyclerViewAdapter(listOfMedicinesForms,this)
    }

    override fun changeForm(form: MedicineFormCard) {
        medicine.formName = form.title
        medicine.formImage = form.photo
    }



    private fun setupMedicineType(){
        binding.medicineTypeChooser.setAdapter(ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,arrayListOf("таблетки", "ml", "mg")))
        binding.medicineTypeChooser.inputType = 0
        binding.medicineTypeChooser.keyListener = null
        binding.medicineTypeChooser.setOnClickListener {
            binding.medicineTypeChooser.showDropDown()
        }

        binding.medicineTypeChooser.setOnItemClickListener { _, _, i, _ ->
            medicine.type = binding.medicineTypeChooser.adapter.getItem(i).toString()
        }

        val helper: Helpers = Helpers()
        helper.keyboardEnterButtonClick(binding.amountInputField) {closeKeyboard()}
    }

    private fun closeKeyboard(){
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }


    private fun setupDateAndTimePicker(){
        (arrayListOf<View>(binding.chooseTimeButton,binding.timeTextInput)).forEach {
            it.setOnClickListener {
                val timePickerDialog: TimePickerHelper = TimePickerHelper()
                timePickerDialog.show(requireActivity().supportFragmentManager, "time_picker")
            }
        }

        (arrayListOf<View>(binding.chooseDateButton,binding.dateTextInput)).forEach {
            it.setOnClickListener {
                val datePickerDialog: DatePickerHelper = DatePickerHelper()
                datePickerDialog.show(requireActivity().supportFragmentManager, "date_picker")
            }
        }

        val dateTimePickerViewModel : DateTimePickerViewModel = ViewModelProvider(requireActivity()).get(DateTimePickerViewModel::class.java)
        val c = Calendar.getInstance()
        dateTimePickerViewModel.setDate(c.timeInMillis)
        dateTimePickerViewModel.setTime(c.timeInMillis)
        //=========================================

        dateTimePickerViewModel.getTime().observe(viewLifecycleOwner, Observer { time->
            val helper = Calendar.getInstance()
            helper.timeInMillis = time

            //set madicine time
            c.set(Calendar.HOUR,helper.get(Calendar.HOUR))
            c.set(Calendar.MINUTE,helper.get(Calendar.MINUTE))
            c.set(Calendar.SECOND,helper.get(Calendar.SECOND))

            medicine.time = c.timeInMillis
            binding.timeTextInput.text = DateFormat.format("HH:mm", helper).toString()
        })

        dateTimePickerViewModel.getDate().observe(viewLifecycleOwner, Observer { date->
            val helper = Calendar.getInstance()
            helper.timeInMillis = date

            //set madicine date
            c.set(Calendar.YEAR,helper.get(Calendar.YEAR))
            c.set(Calendar.MONTH,helper.get(Calendar.MONTH))
            c.set(Calendar.DAY_OF_MONTH,helper.get(Calendar.DAY_OF_MONTH))

            medicine.time = c.timeInMillis
            binding.dateTextInput.text = DateFormat.format("dd MMMM yyyy", helper).toString()
        })
    }



    private fun insertMedicine(){
        val helpers: Helpers = Helpers()

        medicine.name = if(binding.medicineNameInput.text.isNullOrEmpty()) "Medicine" else binding.medicineNameInput.text.toString()
        medicine.amount = if(binding.amountInputField.text.isNullOrEmpty()) "Blank" else binding.amountInputField.text.toString()

        try{
            if(medicine.time + 100000 > System.currentTimeMillis()){
                helpers.showSnackBar("Saved", requireView())

                val c = Calendar.getInstance()
                c.timeInMillis = medicine.time


                val duration = binding.durationPicker.value
                val unit = binding.unitSpinner.selectedItemPosition

                // Преобразование продолжительности в дни
                val totalDurationInDays = when (unit) {
                    0 -> duration // Если выбраны дни, то продолжительность равна выбранному количеству дней
                    1 -> duration * 7 // Если выбраны недели, то продолжительность в днях равна выбранному количеству недель, умноженному на 7
                    2 -> duration * 30 // Если выбраны месяцы, то продолжительность в днях равна выбранному количеству месяцев, умноженному на 30
                    else -> duration // По умолчанию
                }

                 for (i in 1..totalDurationInDays) {
                    val medicineToSave = Medicine(medicine.name, medicine.amount, medicine.type, c.timeInMillis, medicine.duration, medicine.formName, medicine.formImage)
                    medicinesViewModel.insertMedicine(medicineToSave)

                    // Установка напоминания на текущий день
                    val intent = Intent(requireActivity().applicationContext, MedicineAlarmReceiver::class.java)
                    intent.apply {
                        putExtra("medicineName", medicine.name)
                        putExtra("medicineAmount", medicine.amount)
                        putExtra("medicineType", medicine.type)
                        putExtra("medicineImage", medicine.formImage)
                    }
                    val alarmIntent = intent.let {
                        PendingIntent.getBroadcast(requireActivity().applicationContext, medicineToSave.time.toInt(), it,
                            PendingIntent.FLAG_IMMUTABLE)
                    }
                    alarmManager.set(AlarmManager.RTC_WAKEUP, medicineToSave.time, alarmIntent)

                    // Переход к следующему дню
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }

                requireActivity().onBackPressed()
            }
            else helpers.showSnackBar("Cannot save medicine which date has already passed",requireView())
        }catch (ex:Exception){
            Log.d("TAG",ex.toString())
            helpers.showSnackBar(ex.message.toString(),requireView())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}