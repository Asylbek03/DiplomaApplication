package com.example.diplomaapplication.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.diplomaapplication.databases.firestore_database.DatabaseError
import com.example.diplomaapplication.helpers.Helpers
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databinding.FragmentAskChatGptBinding
import com.example.diplomaapplication.databinding.FragmentChatBinding
import com.example.diplomaapplication.databinding.FragmentPrescribeMedicinesBinding
import com.example.diplomaapplication.model.Request
import com.example.diplomaapplication.model.views.CurrentUserViewModel
import com.example.diplomaapplication.model.views.RequestViewModel
import com.example.diplomaapplication.views.medicines.PrescriptionDialogFragment

class AskChatGPTFragment : DialogFragment(), DatabaseError {

    private var _binding: FragmentAskChatGptBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentUserViewModel: CurrentUserViewModel
    private lateinit var requestViewModel: RequestViewModel
    private lateinit var request: Request

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAskChatGptBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    companion object {
        fun newInstance(currentUserViewModel: CurrentUserViewModel, requestViewModel: RequestViewModel, request: Request): AskChatGPTFragment {
            val fragment = AskChatGPTFragment()
            fragment.currentUserViewModel = currentUserViewModel
            fragment.requestViewModel = requestViewModel
            fragment.request = request
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка кнопки отправки
        binding.sendButton.setOnClickListener {
            // Получаем вопрос из текстового поля
            val question = binding.questionEditText.text.toString().trim()

            // Проверяем, что вопрос не пустой
            if (question.isNotEmpty()) {
                // TODO: Отправить вопрос на обработку ChatGPT и получить ответ

                // Временный код для демонстрации
                val answer = "Ответ от ChatGPT"

                // TODO: Отправить ответ в чат

                // Закрыть диалоговое окно
                dismiss()
            } else {
                // Если вопрос пустой, показать сообщение об ошибке
                errorHandled("Введите вопрос", view)
            }
        }
    }

    override fun errorHandled(errorMessage: String, view: View) {
        Helpers().showSnackBar(errorMessage, requireView())
    }
}
