package com.example.diplomaapplication.ui.chat

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.databases.firestore_database.FireStoreDatabase
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine
import com.example.diplomaapplication.databinding.FragmentChatBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.Message
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel
import com.example.diplomaapplication.recycler_views.adapter.MessagesRecyclerViewAdapter
import com.example.diplomaapplication.views.doctor.ChatInterface
import com.example.diplomaapplication.views.doctor.ConfirmDialog
import com.example.diplomaapplication.views.medicines.AddMedicineFragmentChat
import com.example.diplomaapplication.views.medicines.PrescriptionDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ChatsFragment : Fragment(), ChatInterface, DatabaseError {

    private var _binding: FragmentChatBinding? = null
    private lateinit var requestViewModel: RequestViewModel
    private lateinit var currentUserViewModel: CurrentUserViewModel


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatsViewModel =
            ViewModelProvider(this).get(ChatsViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root


        currentUserViewModel = ViewModelProvider(requireActivity()).get(CurrentUserViewModel::class.java)
        requestViewModel =  ViewModelProvider(requireActivity()).get(RequestViewModel::class.java)

        setChatInfo()
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        Helpers().keyboardEnterButtonClick(binding.messageTextInput){
            closeKeyboard()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

//        binding.prescribeMedicineButton.setOnClickListener {
//            val currentUser = currentUserViewModel.getUser().value
//            if (currentUser?.isDoctor == true) {
//                findNavController().navigate(R.id.action_chatFragment_to_prescribeMedicinesFragment)
//            } else {
//                Toast.makeText(requireContext(), "Only doctors can prescribe medicine", Toast.LENGTH_SHORT).show()
//            }
//        }






        Helpers().keyboardEnterButtonClick(binding.messageTextInput){
            closeKeyboard()
        }
    }


    private fun setChatInfo(){
        val database = FireStoreDatabase()
        requestViewModel.getRequest().observe(viewLifecycleOwner, Observer { requestId ->
            database.getRequestById(requireView(), requestId, this)
        })
    }

    override fun onRequestChanged(request: Request) {
        try{
            currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { currentUser->
                binding.sendMessageButton.setOnClickListener {
                    request.messages.add(Message(binding.messageTextInput.text.toString(),currentUser!!.id))
                    binding.messageTextInput.text.clear()
                    closeKeyboard()
                    FireStoreDatabase().insertRequest(request,requireView(),this)
                }

                binding.exitChatButton.setOnClickListener {
                    ConfirmDialog("Подтвердить","Вы хотите выйти?", currentUser!!.isDoctor,request,this).show(requireActivity().supportFragmentManager,"confirm")
                }

                binding.prescribeMedicineButton.setOnClickListener {
                    if (currentUser?.isDoctor == true) {
                        val dialogFragment = PrescriptionDialogFragment.newInstance(currentUserViewModel, requestViewModel, request)
                        dialogFragment.show(childFragmentManager, "PrescriptionDialogFragment")
                    } else {
                        val dialogFragment = AddMedicineFragmentChat.newInstance(currentUserViewModel, requestViewModel, request)
                        dialogFragment.show(childFragmentManager, "AddMedicineFragmentChat")
                    }
                }

                if(currentUser!!.isDoctor){
                    binding.chatMemberName.text = request.patient?.firstName
                    binding.chatMemberBio.text = request.patient?.bio
                }else{
                    binding.chatMemberName.text = request.doctor?.firstName
                    binding.chatMemberBio.text = request.doctor?.medicineBranch
                    if(request.isDoctorActive){
                        binding.messagesRecyclerView.visibility = View.VISIBLE
                        binding.messageTextInput.visibility = View.VISIBLE
                        binding.sendMessageButton.visibility = View.VISIBLE
                        binding.waitingForDoctorText.visibility = View.GONE
                    }else{
                        binding.messagesRecyclerView.visibility = View.GONE
                        binding.messageTextInput.visibility = View.GONE
                        binding.sendMessageButton.visibility = View.GONE
                        binding.waitingForDoctorText.visibility = View.VISIBLE
                    }
                }
                binding.messagesRecyclerView.adapter = MessagesRecyclerViewAdapter(currentUser.id, request.messages)
                setChatInfo()
            })
        }catch (ex: Exception) {
            Log.e("ChatFragment", "Error in onRequestChanged: ${ex.message}", ex)
        }
    }


    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage,requireView())
    }

    private fun closeKeyboard(){
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDeleteChat(requestId: String) {
        FireStoreDatabase().deleteAndRemoveValueEventListenerFromRequest(requestId)
    }

    override fun onDoctorLeave(request: Request) {
        request.isDoctorActive = false
        FireStoreDatabase().insertRequest(request,requireView(),this)
        findNavController().navigate(R.id.action_chatFragment_to_medicinesFragment)
    }

    override fun onPatientDisabledChat() {
        try{
            currentUserViewModel.getUser().observe(viewLifecycleOwner, Observer { currentUser->
                Helpers().showSnackBar(if(currentUser!!.isDoctor) "Чат отменен пользователем" else "Вы вышли из чата",requireView())
                findNavController().navigate(R.id.action_chatFragment_to_medicinesFragment)

            })

        }catch (_:java.lang.Exception){}
    }

}