package com.example.diplomaapplication.views.profile.user_config

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diplomaapplication.R
import com.example.diplomaapplication.authentication.Authentication
import com.example.diplomaapplication.databinding.FragmentChangeBioBinding
import com.example.diplomaapplication.databinding.FragmentChangePasswordBinding
import com.example.diplomaapplication.helpers.Helpers


class ChangePassword : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        binding.saveNewPasswordButton.setOnClickListener {
            changePassword()
        }

        //soft keyboard enter click
        Helpers().keyboardEnterButtonClick(binding.newPasswordInput){binding.saveNewPasswordButton.performClick()}
    }

    private fun setupNavigation() = binding.changePasswordBackButton.setOnClickListener { requireActivity().onBackPressed() }

    //------------------------| Change the password |--------------------------
    private fun changePassword(){
        Authentication().changePassword(binding.oldPasswordInput.text.toString(),binding.newPasswordInput.text.toString(),requireView()){
            requireActivity().onBackPressed()
        }
    }
    //=========================================================================
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}