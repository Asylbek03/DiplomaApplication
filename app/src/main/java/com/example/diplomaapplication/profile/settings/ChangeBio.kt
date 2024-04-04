package com.example.diplomaapplication.profile.settings

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
import com.example.diplomaapplication.databinding.FragmentAddMedicineBinding
import com.example.diplomaapplication.databinding.FragmentChangeBioBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.views.CurrentUserViewModel


class ChangeBio : Fragment() , DatabaseError {
    private lateinit var currentUserViewModel: CurrentUserViewModel
    private var _binding: FragmentChangeBioBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangeBioBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)

        binding.changeBioBackButton.setOnClickListener { requireActivity().onBackPressed() }
        saveNewBio()
    }



    private fun saveNewBio() {
        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { user ->
            binding.changeBioInput.setText(user!!.bio)
            binding.saveNewBioButton.setOnClickListener {
                user.bio = binding.changeBioInput.text.toString()
                val db: FireStoreDatabase = FireStoreDatabase()
                db.insertUserToDatabase(user, requireView(), this)
                requireActivity().onBackPressed()
            }
            Helpers().keyboardEnterButtonClick(binding.changeBioInput) { binding.saveNewBioButton.performClick() }
        })
    }

    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage, requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
