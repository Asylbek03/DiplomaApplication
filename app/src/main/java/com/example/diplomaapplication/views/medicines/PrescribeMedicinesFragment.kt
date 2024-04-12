package com.example.diplomaapplication.views.medicines

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.room_database.medicines_database.Medicine
import com.example.diplomaapplication.databinding.FragmentChatBinding
import com.example.diplomaapplication.databinding.FragmentPrescribeMedicinesBinding
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel
import com.example.diplomaapplication.ui.chat.ChatsViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PrescribeMedicinesFragment : Fragment() {

}