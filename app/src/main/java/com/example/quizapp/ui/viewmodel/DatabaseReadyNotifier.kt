package com.example.quizapp.utils

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DatabaseReadyNotifier {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready

    fun markReady() {
        Log.d("DatabaseReadyNotifier", "🟢 Base de datos NOTIFICADA como lista")
        _ready.value = true
    }
}
