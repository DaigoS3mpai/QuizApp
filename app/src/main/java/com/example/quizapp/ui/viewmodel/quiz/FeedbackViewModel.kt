package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import com.example.quizapp.data.repository.FeedbackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val repo: FeedbackRepository = FeedbackRepository(),
    // 👇 dispatcher inyectable para test
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _misFeedback = MutableStateFlow<List<FeedbackResponseDto>>(emptyList())
    val misFeedback: StateFlow<List<FeedbackResponseDto>> = _misFeedback

    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun cargarMisFeedback(userId: Long) {
        viewModelScope.launch(ioDispatcher) {   // 👈 antes era Dispatchers.IO
            try {
                val lista = repo.listarPorUsuario(userId)
                _misFeedback.value = lista
                _mensajeError.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _mensajeError.value = "Error al cargar feedback: ${e.message}"
            }
        }
    }

    fun enviarFeedback(
        userId: Long,
        mensaje: String,
        tipo: String,
        destino: String
    ) {
        viewModelScope.launch(ioDispatcher) {   // 👈 antes era Dispatchers.IO
            _enviando.value = true
            try {
                repo.enviarFeedback(
                    userId,
                    mensaje,
                    tipo,
                    destino
                )
                // recargar lista después de enviar
                cargarMisFeedback(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                _mensajeError.value = "Error al enviar feedback: ${e.message}"
            } finally {
                _enviando.value = false
            }
        }
    }
}

class FeedbackViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
            return FeedbackViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}