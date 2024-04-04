package com.example.diplomaapplication.profile.settings

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentChangeWorkTimeBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RegisterTimeViewModel
import com.example.diplomaapplication.views.auth.RegisterTimePickerDialog
import java.util.Calendar


class ChangeWorkTime : Fragment(), DatabaseError {
    private lateinit var user: User
    private lateinit var registerTimeViewModel: RegisterTimeViewModel
    private var _binding: FragmentChangeWorkTimeBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUserViewModel: CurrentUserViewModel
    private var startTimeMillis: Long = 0
    private var endTimeMillis: Long = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangeWorkTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerTimeViewModel = ViewModelProvider(requireActivity()).get(RegisterTimeViewModel::class.java)
        user = User(System.currentTimeMillis().toString(),"Name","A doctor", R.drawable.doctor_avatar_1, true, " " , System.currentTimeMillis(), System.currentTimeMillis(),0.0f)

        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)
        binding.startTimeInput.setOnClickListener {
            val dialog = RegisterTimePickerDialog(true)
            dialog.show(requireActivity().supportFragmentManager,"start_time_dialog")
        }

        binding.endTimeInput.setOnClickListener {
            val dialog = RegisterTimePickerDialog(false)
            dialog.show(requireActivity().supportFragmentManager,"end_time_dialog")
        }
        setupStartAndEndWorkTime()
        saveNewWorkTime()
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.changeWorkTimeBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun saveNewWorkTime() {
        val calendar = Calendar.getInstance()
        val db: FireStoreDatabase = FireStoreDatabase()

        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
            binding.startTimeInput.text = DateFormat.format("HH:mm", calendar).toString()
            binding.endTimeInput.text = DateFormat.format("HH:mm", calendar).toString()
            binding.btnChangeWorkTime.setOnClickListener {

                if (user != null) {
                    user.startTime = startTimeMillis
                    user.endTime = endTimeMillis

                    db.insertUserToDatabase(user, requireView(), this)
                }
                requireActivity().onBackPressed()
            }
        })
    }


    private fun setupStartAndEndWorkTime(){
        val calendar = Calendar.getInstance()

        registerTimeViewModel.getStartTime().observe(viewLifecycleOwner, Observer { startTime->
            calendar.timeInMillis = startTime
            startTimeMillis = startTime
            binding.startTimeInput.text = DateFormat.format("HH:mm", calendar).toString()
            user.startTime = startTime
        })

        registerTimeViewModel.getEndTime().observe(viewLifecycleOwner, Observer { endTime->
            calendar.timeInMillis = endTime
            endTimeMillis = endTime
            binding.endTimeInput.text = DateFormat.format("HH:mm", calendar).toString()
            user.endTime = endTime
        })

        setStartTimeValues()


        binding.startTimeInput.setOnClickListener {
            val dialog = RegisterTimePickerDialog(true)
            dialog.show(requireActivity().supportFragmentManager,"start_time_dialog")
        }

        binding.endTimeInput.setOnClickListener {
            val dialog = RegisterTimePickerDialog(false)
            dialog.show(requireActivity().supportFragmentManager,"end_time_dialog")
        }
    }

    private fun setStartTimeValues(){
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY,8)
        c.set(Calendar.MINUTE,0)
        registerTimeViewModel.setStartTime(c.timeInMillis)
        c.set(Calendar.HOUR_OF_DAY,12)
        c.set(Calendar.MINUTE,0)
        registerTimeViewModel.setEndTime(c.timeInMillis)
    }
    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage, requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}