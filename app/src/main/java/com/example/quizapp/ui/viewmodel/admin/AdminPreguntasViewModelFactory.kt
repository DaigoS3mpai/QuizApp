package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AdminPreguntasViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPreguntasViewModel::class.java)) {
            return AdminPreguntasViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
