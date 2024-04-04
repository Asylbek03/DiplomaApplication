package com.example.diplomaapplication.views.medicines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databases.medicines_database.MedicinesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClearMedicine: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clear_medicine, container, false)

        view.findViewById<Button>(R.id.btnConfirmClearMedicine).setOnClickListener {
            clearDatabase()
        }

        return view
    }

    private fun clearDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            context?.let {
                MedicinesDatabase.clearDatabase(it.applicationContext)
            }
        }
    }

    override fun onDestroy() {
        clearDatabase()
        super.onDestroy()
    }
}
