package com.example.quizapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeedbackViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedbackViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
