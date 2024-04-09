package com.example.diplomaapplication.views.profile.user_config

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databinding.FragmentChangeBioBinding
import com.example.diplomaapplication.databinding.FragmentChooseAvatarBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.User
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import java.lang.Exception


class ChooseAvatarFragment : Fragment(), DatabaseError {
    private var _binding: FragmentChooseAvatarBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUserViewModel: CurrentUserViewModel
    private var chooseAvatarResource : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChooseAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)
        setupNavigation()
        setupImages()
    }

    private fun setupNavigation() {
        R.layout.fragment_choose_avatar
        binding.changeAvatarBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.saveAvatarButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    private fun setupImages() {

        val listOfAvatarsImageViews = arrayListOf<ImageView>(
            binding.avatar1, binding.avatar2, binding.avatar3, binding.avatar4, binding.avatar5, binding.avatar6
        )

        val listOfDoctorsImages = arrayListOf<Int>(
            R.drawable.avatar_doctor_1,
            R.drawable.avatar_doctor_2,
            R.drawable.avatar_doctor_3,
            R.drawable.avatar_doctor_4,
            R.drawable.avatar_doctor_5,
            R.drawable.avatar_doctor_6
        )

        val listOfPatientsImages = arrayListOf<Int>(
            R.drawable.avatar_user_1,
            R.drawable.avatar_user_2,
            R.drawable.avatar_user_3,
            R.drawable.avatar_user_4,
            R.drawable.avatar_user_5,
            R.drawable.avatar_user_6
        )


        currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { user->
            try{
                user!!.avatar?.let { binding.chooseAvatar.setImageResource(it) }
                for(i in 0 until listOfAvatarsImageViews.size){
                    listOfAvatarsImageViews[i].setImageResource(if(user.isDoctor) listOfDoctorsImages[i] else listOfPatientsImages[i])

                    listOfAvatarsImageViews[i].setOnClickListener {
                        binding.chooseAvatar.setImageResource(if(user.isDoctor) listOfDoctorsImages[i] else listOfPatientsImages[i])
                        chooseAvatarResource = if(user.isDoctor) listOfDoctorsImages[i] else listOfPatientsImages[i]
                    }
                }
                binding.saveAvatarButton.setOnClickListener { saveAvatar(user) }
            }catch (ex:Exception){}

        })
    }

    private fun saveAvatar(user: User){
        user.avatar = chooseAvatarResource
        val db:FireStoreDatabase = FireStoreDatabase()
        db.insertUserToDatabase(user,requireView(),this)
        requireActivity().onBackPressed()
    }

    override fun errorHandled(errorMessage: String, view: View) {
       Helpers().showSnackBar(errorMessage,requireView())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}