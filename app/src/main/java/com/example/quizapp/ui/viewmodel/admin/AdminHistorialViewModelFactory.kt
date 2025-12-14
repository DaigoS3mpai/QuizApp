package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdminHistorialViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminHistorialViewModel::class.java)) {
            return AdminHistorialViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}