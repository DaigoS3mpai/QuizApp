package com.example.quizapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdminPreguntasViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPreguntasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminPreguntasViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
