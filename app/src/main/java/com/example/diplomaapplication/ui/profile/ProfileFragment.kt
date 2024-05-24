package com.example.diplomaapplication.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.diplomaapplication.R
import com.example.diplomaapplication.authentication.Authentication
import com.example.diplomaapplication.databinding.FragmentProfileBinding
import com.example.diplomaapplication.model.views.CurrentUserViewModel

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var  currentUserViewModel: CurrentUserViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        auth = Firebase.auth
//
//        binding.btnSignOut.setOnClickListener {
//            auth.signOut()
//
//            val intent = Intent(requireActivity(), SignInActivity::class.java)
//            startActivity(intent)
//
//            finish()
//        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)

        //val bottom_nav: BottomNavigationView = view.findViewById(R.id.navView)
        val changeAvatarBox: LinearLayout = view.findViewById(R.id.btnChangeAvatar)
        val logOutBox: LinearLayout = view.findViewById(R.id.btnSignOut)


        changeAvatarBox.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_chooseAvatarFragment)
        }

        logOutBox.setOnClickListener {
            Authentication().signOutFromFirebase(requireView())
        }

        setupUserInfo()
        boxesClick()

    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setupUserInfo(){

        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { user->
            if(user!=null){
                binding.currentUserName.text = user.firstName
                user.avatar?.let { binding.currentUserAvatar.setImageResource(it) }
                binding.currentUserBio.text = if(user.isDoctor) user.medicineBranch else user.bio

                val startTime = user.startTime?.let { Date(it) }
                    ?.let { SimpleDateFormat("HH:mm").format(it) }
                val endTime = user.endTime?.let { Date(it) }
                    ?.let { SimpleDateFormat("HH:mm").format(it) }

                binding.tvWorkTime.text = "$startTime - $endTime"
                if(!user.isDoctor){
                    binding.changeMedicineBranchDivider.visibility = View.GONE
                    binding.btnChangeMedicineBranch.visibility = View.GONE
                    binding.btnChangeWorkTime.visibility = View.GONE
                    binding.tvWorkTime.visibility = View.GONE
                }
            }

        })
    }

    private fun boxesClick(){
        binding.btnChangeName.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeName)
        }
        binding.btnChangeBio.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeBio)
        }

        binding.btnChangeMedicineBranch.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeMedicineBranch)
        }

        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePassword)
        }

        binding.btnChangeWorkTime.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeWorkTime)
        }

        binding.btnClearMedicine.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_clearMedicine)
        }

        binding.btnSetInfo.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_setInfo)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
