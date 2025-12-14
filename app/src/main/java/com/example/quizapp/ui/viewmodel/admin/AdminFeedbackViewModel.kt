package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import com.example.quizapp.data.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class FeedbackFiltro {
    PENDIENTES, TODOS
}

data class AdminFeedbackUiState(
    val items: List<FeedbackResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val filtro: FeedbackFiltro = FeedbackFiltro.PENDIENTES
)

class AdminFeedbackViewModel(
    private val repo: FeedbackRepository = FeedbackRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminFeedbackUiState())
    val uiState: StateFlow<AdminFeedbackUiState> = _uiState

    init {
        cargarFeedbackPendientes()
    }

    fun cambiarFiltro(nuevo: FeedbackFiltro) {
        _uiState.value = _uiState.value.copy(filtro = nuevo)
        when (nuevo) {
            FeedbackFiltro.PENDIENTES -> cargarFeedbackPendientes()
            FeedbackFiltro.TODOS -> cargarFeedbackTodos()
        }
    }

    fun cargarFeedbackPendientes() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null)
            try {
                val lista = repo.listarPendientes()
                _uiState.value = _uiState.value.copy(
                    items = lista,
                    isLoading = false,
                    errorMsg = if (lista.isEmpty()) "No hay feedback pendiente." else null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al cargar pendientes: ${e.message}"
                )
            }
        }
    }

    fun cargarFeedbackTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null)
            try {
                val lista = repo.listarTodos()
                _uiState.value = _uiState.value.copy(
                    items = lista,
                    isLoading = false,
                    errorMsg = if (lista.isEmpty()) "No hay feedback registrado." else null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al cargar feedback: ${e.message}"
                )
            }
        }
    }

    fun resolverFeedback(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.resolver(id)
                // recargar según filtro actual
                when (_uiState.value.filtro) {
                    FeedbackFiltro.PENDIENTES -> cargarFeedbackPendientes()
                    FeedbackFiltro.TODOS -> cargarFeedbackTodos()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMsg = "Error al resolver feedback: ${e.message}"
                )
            }
        }
    }
}