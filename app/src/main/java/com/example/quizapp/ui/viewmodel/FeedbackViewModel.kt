package com.example.quizapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.database.AppDatabase
import com.example.quizapp.data.feedback.FeedbackConUsuario
import com.example.quizapp.data.feedback.FeedbackDao
import com.example.quizapp.data.feedback.FeedbackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackViewModel(context: Context) : ViewModel() {

    private val db = AppDatabase.getInstance(context.applicationContext)
    private val feedbackDao: FeedbackDao = db.feedbackDao()

    private val _misFeedback = MutableStateFlow<List<FeedbackEntity>>(emptyList())
    val misFeedback: StateFlow<List<FeedbackEntity>> = _misFeedback

    private val _todosFeedback = MutableStateFlow<List<FeedbackConUsuario>>(emptyList())
    val todosFeedback: StateFlow<List<FeedbackConUsuario>> = _todosFeedback

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando

    private fun fechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun cargarMisFeedback(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _misFeedback.value = feedbackDao.getByUsuario(userId)
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun cargarTodosFeedback(soloPendientes: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _todosFeedback.value = feedbackDao.getTodosConUsuario(
                    soloPendientes = if (soloPendientes) 1 else 0
                )
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun enviarFeedback(
        userId: Int,
        mensaje: String,
        tipo: String,
        destino: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _enviando.value = true

                val nuevo = FeedbackEntity(
                    mensaje = mensaje,
                    tipo = tipo,
                    destino = destino,
                    fecha = fechaActual(),
                    usuario_id_usuario = userId
                )

                feedbackDao.insert(nuevo)

                // Recargar lista del usuario
                _misFeedback.value = feedbackDao.getByUsuario(userId)
                _enviando.value = false
            } catch (e: Exception) {
                _enviando.value = false
                _mensajeError.value = e.message
            }
        }
    }

    fun marcarResuelto(idFeedback: Int, soloPendientes: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                feedbackDao.marcarResuelto(idFeedback)
                _todosFeedback.value = feedbackDao.getTodosConUsuario(
                    soloPendientes = if (soloPendientes) 1 else 0
                )
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }
}
