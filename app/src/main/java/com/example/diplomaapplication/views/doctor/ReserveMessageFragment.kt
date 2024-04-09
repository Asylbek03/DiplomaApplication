package com.example.diplomaapplication.views.doctor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.diplomaapplication.R

import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentAllDoctorsBinding
import com.example.diplomaapplication.databinding.FragmentReserveMessageBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel


class ReserveMessageFragment : Fragment(), DatabaseError {

    private var _binding: FragmentReserveMessageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReserveMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupDoctorInfo()
    }

    private fun setupNavigation() =  binding.reserveMessageBackButton.setOnClickListener { requireActivity().onBackPressed() }

    private fun setupDoctorInfo(){
        val chooseDoctorViewModel: ChooseDoctorViewModel = ViewModelProvider(requireActivity()).get(ChooseDoctorViewModel::class.java)

        chooseDoctorViewModel.getDoctor().observe(viewLifecycleOwner, Observer { doctor->
            doctor.avatar?.let { binding.chooseDoctorAvatar.setImageResource(it) }
            binding.chooseDoctorName.text = doctor.firstName
            binding.chooseDoctorMedicineBranch.text = doctor.medicineBranch
            binding.chooseDoctorBio.text = doctor.bio
            binding.chooseDoctorStarCount.text = doctor.starCount.toString()

            binding.reserveMessageButton.setOnClickListener {
                insertRequestToDatabase(doctor)
            }

        })
    }

    private fun insertRequestToDatabase(doctor: User){
        val currentUserViewModel: CurrentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)
        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer {
            val patientId = it!!.id
            val request: Request = Request(patientId+doctor.id,it,doctor,
                isUserActive =true,
                isDoctorActive = false,
                messages = arrayListOf()
            )
            val db: FireStoreDatabase = FireStoreDatabase()
            db.insertRequest(request,requireView(),this)

            val requestViewModel: RequestViewModel = ViewModelProvider(requireActivity()).get(RequestViewModel::class.java)
            requestViewModel.setRequest(request.id)

            findNavController().navigate(R.id.action_reserveMessageFragment_to_chatFragment)
        })
    }

    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage,view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}