package com.example.diplomaapplication.views.profile.user_config

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentChangeNameBinding
import com.example.diplomaapplication.databinding.FragmentLoginBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.views.CurrentUserViewModel



class ChangeName : Fragment(), DatabaseError {

    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUserViewModel: CurrentUserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)

        setupNavigation()
        saveNewName()
    }

    //setup nav
    private fun setupNavigation() = binding.changeNameBackButton.setOnClickListener {
        requireActivity().onBackPressed()
    }


    //-----------------------------| Save new username in database |---------------------------------
    private fun saveNewName(){
        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer {user->
            binding.changeNameInput.setText(user!!.firstName)
            binding.saveNewNameButton.setOnClickListener {
                user.firstName = binding.changeNameInput.text.toString()
                val db: FireStoreDatabase = FireStoreDatabase()
                db.insertUserToDatabase(user,requireView(),this)
                requireActivity().onBackPressed()
            }
            //soft keyboard enter click
            Helpers().keyboardEnterButtonClick(binding.changeNameInput){binding.saveNewNameButton.performClick()}
        })
    }
    //===============================================================================================

    //handle eventual error
    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage,requireView())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}