package com.example.diplomaapplication.views.profile.user_config

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentChangeBioBinding
import com.example.diplomaapplication.databinding.FragmentChangeMedicineBranchBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.views.CurrentUserViewModel


class ChangeMedicineBranch : Fragment(), DatabaseError {

    private var _binding: FragmentChangeMedicineBranchBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangeMedicineBranchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupDoctorsTypesAutoCompleteTextView()
    }

    //setup Navigation
    private fun setupNavigation() = binding.changeMedicineBranchBackButton.setOnClickListener { requireActivity().onBackPressed() }

    //------------------------| Setup auto complete text view with doctors types |-------------------------
    private fun setupDoctorsTypesAutoCompleteTextView(){
        val currentUserViewModel : CurrentUserViewModel = ViewModelProvider(requireActivity()).get(
            CurrentUserViewModel::class.java)

        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer {user->
            binding.newMedicineBranchACTV.setText(user!!.medicineBranch,false)

            binding.newMedicineBranchACTV.setAdapter( ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,
                arrayListOf("Педиатр","Семейный врач","Кардиолог","Дерматолог","Психиатр","Невролог"))
            )
            binding.newMedicineBranchACTV.inputType = 0
            binding.newMedicineBranchACTV.keyListener = null
            binding.newMedicineBranchACTV.setOnClickListener {
                binding.newMedicineBranchACTV.showDropDown()
            }

            //change medicine branch of user
            binding.newMedicineBranchACTV.setOnItemClickListener { _, _, i, _ ->
                user.medicineBranch = binding.newMedicineBranchACTV.adapter.getItem(i).toString()
            }

            //save new user branch into database
            binding.saveNewMedicineBranchButton.setOnClickListener {
                FireStoreDatabase().insertUserToDatabase(user,requireView(),this)
                requireActivity().onBackPressed()
            }

        })
    }


    //==========================================================================================================

    //handle error
    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage,requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}