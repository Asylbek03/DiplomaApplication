package com.example.diplomaapplication.views.auth

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.authentication.Authentication
import com.example.diplomaapplication.databinding.FragmentRegisterBinding
import com.example.diplomaapplication.databinding.FragmentWelcomeAuthBinding
import androidx.lifecycle.Observer
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.RegisterTimeViewModel
import java.util.*


class RegisterFragment : Fragment() {

    private lateinit var user: User
    private lateinit var registerTimeViewModel: RegisterTimeViewModel
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerTimeViewModel = ViewModelProvider(requireActivity()).get(RegisterTimeViewModel::class.java)

        user = User(
            id = System.currentTimeMillis().toString(),
            firstName = "Name",
            bio = "A Doctor",
            avatar = R.drawable.avatar_doctor_1,
            isDoctor = true,
            medicineBranch = " ",
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis(),
            starCount = 0.0f
        )
        setupNavigation()
        binding.registerButton.setOnClickListener { registerWithEmailAndPassword() }
        onEnterClicked()
        setUserTypeButtons()
        setupDoctorsTypesAutoCompleteTextView()
        setupStartAndEndWorkTime()
    }

    private fun setupNavigation() {
        binding.registerBackButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun registerWithEmailAndPassword() {
        user.firstName = binding.registerFullNameInput.text.toString()
        user.bio = if(user.isDoctor) "A Doctor" else "A patient"
        user.avatar =  if(user.isDoctor) R.drawable.avatar_doctor_1 else R.drawable.avatar_user_1

        val password = binding.userPasswordInput.text.toString()
        val confirmPassword = binding.userConfirmPasswordInput.text.toString()

        if (password != confirmPassword) {
            Helpers().showSnackBar("Пароли не совпадают", requireView())
            return
        }

        if(user.startTime!! > user.endTime!!){
            Log.d("TIME",user.startTime.toString())
            Log.d("TIME",user.endTime.toString())
            Helpers().showSnackBar("Время начала вашей работы меньше времени окончания!", requireView())
        }else{
            val authentication = Authentication()
            authentication.registerWithEmailAndPassword(binding.userEmailInput.text.toString(), binding.userPasswordInput.text.toString(),
                requireView(), user)
        }

    }

    private fun onEnterClicked(){
        Helpers().keyboardEnterButtonClick(binding.userPasswordInput){
            binding.userPasswordInput.clearFocus()
            val imm: InputMethodManager? =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }


    private fun setUserTypeButtons(){
        val listOfDoctorViews = arrayListOf<View>(binding.doctorTypes, binding.startTimeInput, binding.endTimeInput,binding.startTimeInput2,binding.timeIntervalText)
        binding.doctorButton.setOnClickListener {
            user.isDoctor = true
            changeColors(binding.doctorButton)
            listOfDoctorViews.forEach { it.visibility = View.VISIBLE}
        }
        binding.patientButton.setOnClickListener {
            user.isDoctor = false
            changeColors(binding.patientButton)
            listOfDoctorViews.forEach { it.visibility = View.GONE}
        }
    }

    private fun changeColors(clickedButton: Button){
        arrayListOf(binding.doctorButton,binding.patientButton).forEach {
            it.backgroundTintList = ContextCompat.getColorStateList( requireContext(),R.color.bg_cyan)
            it .setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGray))
        }
        clickedButton.backgroundTintList = ContextCompat.getColorStateList( requireContext(),R.color.colorPrimary)
        clickedButton .setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
    }

    private fun setupDoctorsTypesAutoCompleteTextView(){
        binding.doctorTypes.setAdapter( ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,
            arrayListOf("Педиатр","Семейный врач","Кардиолог","Дерматолог","Психиатр","Невролог")))
        binding.doctorTypes.inputType = 0
        binding.doctorTypes.keyListener = null
        binding.doctorTypes.setOnClickListener {
            binding.doctorTypes.showDropDown()
        }

        binding.doctorTypes.setOnItemClickListener { _, _, i, _ ->
            user.medicineBranch = binding.doctorTypes.adapter.getItem(i).toString()
        }
    }


    private fun setupStartAndEndWorkTime(){
        val calendar = Calendar.getInstance()

        registerTimeViewModel.getStartTime().observe(viewLifecycleOwner, Observer { startTime->
            calendar.timeInMillis = startTime
            binding.startTimeInput.text = DateFormat.format("HH:mm", calendar).toString()
            user.startTime = startTime
        })

        registerTimeViewModel.getEndTime().observe(viewLifecycleOwner, Observer { endTime->
            calendar.timeInMillis = endTime
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}