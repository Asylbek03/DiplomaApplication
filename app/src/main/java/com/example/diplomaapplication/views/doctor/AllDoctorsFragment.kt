package com.example.diplomaapplication.views.doctor

import android.os.Bundle
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
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databinding.FragmentAllDoctorsBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.DoctorTypeCard
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel
import com.example.diplomaapplication.recycler_views.adapter.DoctorTypesRecyclerViewAdapter
import com.example.diplomaapplication.recycler_views.adapter.DoctorsRecyclerViewAdapter
import com.example.diplomaapplication.recycler_views.adapter.PatientsRecyclerViewAdapter

import java.util.*


class AllDoctorsFragment : Fragment(), AllDoctorsInterface, DatabaseError {

    private lateinit var fireStoreDatabase: FireStoreDatabase
    private lateinit var allDoctors: ArrayList<User>
    private var _binding: FragmentAllDoctorsBinding? = null
    private val binding get() = _binding!!
    private var chooseDoctorType = "Педиатр"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireStoreDatabase = FireStoreDatabase()

        binding.doctorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentUserViewModel: CurrentUserViewModel =
            ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)


        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
            try {
                setupUserData(user!!.firstName, user.isDoctor)
                if (!user.isDoctor) {
                    allDoctors = ArrayList()
                    binding.doctorsTypeRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                    binding.doctorsTypeRecyclerView.adapter =
                        DoctorTypesRecyclerViewAdapter(setupDoctorsTypesCards(), this)
                    fireStoreDatabase.getActiveDoctors(requireView(), this)
                } else {
                    binding.doctorsAviableText.text = "Нет доступных пациентов"
                    binding.doctorsTypeRecyclerView.visibility = View.GONE
                    fireStoreDatabase.getRequests(requireView(), this, user.id)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        })
    }



    private fun setupDoctorsTypesCards(): ArrayList<DoctorTypeCard> {
        return arrayListOf(
            DoctorTypeCard("Педиатр", R.drawable.icon_pediatrican_1, true),
            DoctorTypeCard("Семейный врач", R.drawable.icon_family_doctor, false),
            DoctorTypeCard("Кардиолог", R.drawable.icon_cardiologist, false),
            DoctorTypeCard("Дерматолог", R.drawable.icon_dermatologist, false),
            DoctorTypeCard("Психиатр", R.drawable.icon_psychiatrist, false),
            DoctorTypeCard("Невролог", R.drawable.icon_neurologist, false)
        )
    }

    override fun changeType(doctorType: String) {
        chooseDoctorType = doctorType
        val listOfChooseBranchDoctors = getListOfDoctorsBasedOnDoctorType()
        binding.doctorsRecyclerView.adapter =
            DoctorsRecyclerViewAdapter(listOfChooseBranchDoctors, this)

        binding.doctorsAviableText.visibility =
            if (listOfChooseBranchDoctors.isNotEmpty()) View.GONE else View.VISIBLE
        binding.doctorsRecyclerView.visibility =
            if (listOfChooseBranchDoctors.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun chooseDoctor(doctor: User) {
        val chooseDoctorViewModel: ChooseDoctorViewModel =
            ViewModelProvider(requireActivity()).get(ChooseDoctorViewModel::class.java)
        chooseDoctorViewModel.setDoctor(doctor)
        findNavController().navigate(R.id.action_allDoctorsFragment_to_reserveMessageFragment)
    }

    override fun onDoctorsDatabaseChanged(allDoctors: ArrayList<User>) {
        this.allDoctors = allDoctors
        try {
            val listOfChooseBranchDoctors = getListOfDoctorsBasedOnDoctorType()
            binding.doctorsRecyclerView.adapter =
                DoctorsRecyclerViewAdapter(listOfChooseBranchDoctors, this)
            binding.doctorsAviableText.visibility =
                if (listOfChooseBranchDoctors.isNotEmpty()) View.GONE else View.VISIBLE
            binding.doctorsRecyclerView.visibility =
                if (listOfChooseBranchDoctors.isNotEmpty()) View.VISIBLE else View.GONE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getListOfDoctorsBasedOnDoctorType(): ArrayList<User> {
        val currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val doctorStartTimeCalendar = Calendar.getInstance()
        val doctorEndTimeCalendar = Calendar.getInstance()

        val listOfChooseBranchDoctors = ArrayList<User>()
        allDoctors.forEach {
            doctorStartTimeCalendar.timeInMillis = it.startTime!!
            doctorEndTimeCalendar.timeInMillis = it.endTime!!

            if (it.medicineBranch == chooseDoctorType &&
                currentHour >= doctorStartTimeCalendar.get(Calendar.HOUR_OF_DAY) &&
                currentHour <= doctorEndTimeCalendar.get(Calendar.HOUR_OF_DAY)
            ) {
                listOfChooseBranchDoctors.add(it)
            }
        }
        return listOfChooseBranchDoctors
    }

    override fun onRequestsDatabaseChanged(allRequests: ArrayList<Request>) {
        try {
            binding.doctorsRecyclerView.adapter =
                PatientsRecyclerViewAdapter(allRequests, this)
            binding.textView17.text = "Ваши пациенты"
            binding.doctorsAviableText.visibility =
                if (allRequests.isNotEmpty()) View.GONE else View.VISIBLE
            binding.doctorsRecyclerView.visibility =
                if (allRequests.isNotEmpty()) View.VISIBLE else View.GONE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onRequestAccept(request: Request) {
        val requestViewModel: RequestViewModel =
            ViewModelProvider(requireActivity()).get(RequestViewModel::class.java)
        request.isDoctorActive = true
        FireStoreDatabase().insertRequest(request, requireView(), this)
        requestViewModel.setRequest(request.id)
        findNavController().navigate(R.id.action_allDoctorsFragment_to_chatFragment)
    }

    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage, requireView())
    }

    private fun setupUserData(firstName: String, isDoctor: Boolean) {
        binding.nameHi.text = "Здравствуй $firstName"
        binding.topDoctorsTV.text = if (isDoctor) "Пациенты" else "Врачи"
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
