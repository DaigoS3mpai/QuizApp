package com.example.quizapp.ui.viewmodel.admin

class AdminJugadoresViewModelFactory(
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminJugadoresViewModel::class.java)) {
            return AdminJugadoresViewModel() as T   // ya no usa context
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
