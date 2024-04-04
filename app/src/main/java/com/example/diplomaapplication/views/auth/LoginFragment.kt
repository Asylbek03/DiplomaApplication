package com.example.diplomaapplication.views.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databinding.FragmentLoginBinding
import com.example.diplomaapplication.databinding.FragmentWelcomeAuthBinding
import com.example.diplomaapplication.helpers.Helpers
import androidx.navigation.fragment.findNavController
import com.example.diplomaapplication.MainPage
import com.example.diplomaapplication.authentication.Authentication


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInButton = view.findViewById<Button>(R.id.signInButton)

        setupNavigation()
        signInButton.setOnClickListener {
            loginWithEmailAndPassword()
        }
        onEnterClicked()
    }

    private fun setupNavigation() {
        binding.loginBackButton.setOnClickListener {
            val intent = Intent(requireActivity(), MainPage::class.java)
            startActivity(intent)

            requireActivity().onBackPressed()
            findNavController().popBackStack()
        }
    }
    private fun loginWithEmailAndPassword() {
        val authentication: Authentication = Authentication()
        authentication.loginWithEmailAndPassword(
            binding.loginEmailInput.text.toString(),
            binding.loginPasswordInput.text.toString(),
            requireView()
        )
    }


    private fun onEnterClicked(){
        Helpers().keyboardEnterButtonClick(binding.loginPasswordInput){
            binding.signInButton.performClick()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}