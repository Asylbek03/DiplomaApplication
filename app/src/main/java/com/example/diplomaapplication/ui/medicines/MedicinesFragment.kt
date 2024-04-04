package com.example.diplomaapplication.ui.medicines

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.authentication.Authentication
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databases.firestore_database.GetCurrentUserInterface
import com.example.diplomaapplication.databases.medicines_database.Medicine
import com.example.diplomaapplication.databinding.FragmentMedicinesBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.helpers.MedicinesCalendar
import com.example.diplomaapplication.medicine_alarm_receiver.MedicineAlarmReceiver
import com.example.diplomaapplication.model.CalendarDay
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.recycler_views.CalendarAdapter
import com.example.diplomaapplication.recycler_views.MedicinesRecyclerViewAdapter
import com.example.diplomaapplication.views.medicines.DeleteMedicineDialog
import com.example.diplomaapplication.views.medicines.DeleteMedicineInterface
import com.example.diplomaapplication.views.medicines.MedicinesArrayListComparator
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception
import java.util.Calendar
import java.util.Collections

class MedicinesFragment : Fragment(), DeleteMedicineInterface, GetCurrentUserInterface {

    private var _binding: FragmentMedicinesBinding? = null
    private lateinit var medicinesViewModel: com.example.diplomaapplication.databases.medicines_database.MedicinesViewModel
    private lateinit var allMedicines: List<Medicine>
    private lateinit var clickedDay: CalendarDay
    private lateinit var alarmManager: AlarmManager
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val startMonth = Calendar.JANUARY
    val startYear = currentYear
    val endMonth = Calendar.DECEMBER
    val endYear = currentYear


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val medicinesViewModel =
            ViewModelProvider(this).get(MedicinesViewModel::class.java)

        _binding = FragmentMedicinesBinding.inflate(inflater, container, false)
        val root: View = binding.root




        binding.addMedicineButton.setOnClickListener {
            findNavController().navigate(R.id.action_medicinesFragment_to_addMedicineFragment)
        }



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickedDay = MedicinesCalendar().getFirstDay()
        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        medicinesViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(
            com.example.diplomaapplication.databases.medicines_database.MedicinesViewModel::class.java)
        binding.medicinesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        //setCalendar(currentMonth, currentYear)
        val listOfCalendarDays: ArrayList<CalendarDay> = MedicinesCalendar().getListOfDays(startMonth,currentYear, endMonth,endYear)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView.layoutManager = layoutManager
        val adapter = CalendarAdapter(listOfCalendarDays)
        binding.calendarRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { calendarDay ->
            setupRecyclerView(calendarDay)
        }

        binding.showStatisticsButton.setOnClickListener {
            findNavController().navigate(R.id.action_medicinesFragment_to_showMedicinesStatistic)
        }

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        listOfCalendarDays.forEachIndexed { index, calendarDay ->
            if (calendarDay.day == currentDay && calendarDay.month == currentMonth && calendarDay.year == currentYear) {
                binding.calendarRecyclerView.scrollToPosition(index)
                return@forEachIndexed
            }
        }

        medicinesViewModel.allMedicines.observe(viewLifecycleOwner, Observer {
            Log.d("TAG",it.toString())
            allMedicines = it
            setupRecyclerView(clickedDay)
        })



        getCurrentUserId()
    }


    private fun setupRecyclerView(date: CalendarDay){
        val filterList = ArrayList<Medicine>()
        allMedicines.forEach { medicine->
            val medicineCalendar = Calendar.getInstance()
            medicineCalendar.timeInMillis = medicine.time
            val medicineDay = medicineCalendar.get(Calendar.DAY_OF_MONTH)
            val medicineMonth = medicineCalendar.get(Calendar.MONTH)

            if(medicineDay==date.day && medicineMonth==date.month){
                filterList.add(medicine)
            }
        }

        Collections.sort(filterList, MedicinesArrayListComparator())

        if(filterList.isEmpty()){
            binding.addFirstMedicineText.visibility = View.VISIBLE
            binding.medicinesRecyclerView.visibility = View.GONE
        }else{
            binding.addFirstMedicineText.visibility = View.GONE
            binding.medicinesRecyclerView.visibility = View.VISIBLE
            binding.medicinesRecyclerView.adapter = MedicinesRecyclerViewAdapter(filterList,this)
        }

    }


    private fun setCalendar(month: Int, year: Int){
        val listOfCalendarDays :ArrayList<CalendarDay> = MedicinesCalendar().getListOfDays(month, year)

        val adapter = binding.calendarRecyclerView.adapter as CalendarAdapter?

        adapter?.let {
            it.setOnItemClickListener { calendarDay ->
                setupRecyclerView(calendarDay)
            }
        }

        adapter?.let {
            it.setOnItemClickListener { calendarDay ->
                listOfCalendarDays.forEach { it.isChoose = false }
                calendarDay.isChoose = true
                it.notifyDataSetChanged()

                setupRecyclerView(calendarDay)
            }
        }

    }

    override fun showDeleteDialog(medicine: Medicine) {
        val helpers: Helpers = Helpers()
        try{
            DeleteMedicineDialog(medicine,this).show(requireActivity().supportFragmentManager,"delete_medicine_dialog")
        }catch (isex:IllegalStateException){
            helpers.showSnackBar("Activity cannot be null",requireView())
        }catch (ex: Exception){
            helpers.showSnackBar(ex.message.toString(),requireView())
        }

    }

    override fun deleteMedicine(medicine: Medicine) {
        medicinesViewModel.deleteMedicine(medicine)

        val intent = Intent(requireActivity().applicationContext, MedicineAlarmReceiver::class.java)

        intent.apply {
            putExtra("medicineName",medicine.name)
            putExtra("medicineAmount",medicine.amount)
            putExtra("medicineType",medicine.type)
            putExtra("medicineImage",medicine.formImage)
        }
        val alarmIntent = intent.let {
            PendingIntent.getBroadcast(requireActivity().applicationContext,medicine.time.toInt(),it,
                PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.cancel(alarmIntent)
    }


    private fun getCurrentUserId(){
        val currentUserViewModel: CurrentUserViewModel = ViewModelProvider(requireActivity()).get(
            CurrentUserViewModel::class.java)

        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer {user->
            if(user==null){
                Log.d("TAG","Wykonało się")
                val authentication = Authentication()
                val id = authentication.getCurrentUserId()
                if(id!=null){
                    FireStoreDatabase().getUserById(requireView(),id,this)
                }else{
                    Helpers().showSnackBar("Something went wrong . Try again later",requireView())
                }
            }else{
                Log.d("TAG",user.id)
            }
        })

    }


    override fun onGetCurrentUser(user: User) {
        try{
            //change bottom nav item text
            if(user.isDoctor)
                requireActivity().findViewById<BottomNavigationView>(R.id.nav_host_fragment_activity_main_page).menu.getItem(1).title = "Patients"
            else
                requireActivity().findViewById<BottomNavigationView>(R.id.nav_host_fragment_activity_main_page).menu.getItem(1).title = "Doctors"

        }catch (ex: Exception){}
        val currentUserViewModel: CurrentUserViewModel = ViewModelProvider(requireActivity()).get(
            CurrentUserViewModel::class.java)
        currentUserViewModel.setUser(user)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}