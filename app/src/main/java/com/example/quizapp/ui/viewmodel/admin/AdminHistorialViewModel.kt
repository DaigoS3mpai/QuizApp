package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.PartidaDto
import com.example.quizapp.data.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminHistorialUiState(
    val userIdText: String = "",
    val partidas: List<PartidaDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class AdminHistorialViewModel(
    private val context: Context
) : ViewModel() {

    private val repo = GameRepository()

    private val _uiState = MutableStateFlow(AdminHistorialUiState())
    val uiState: StateFlow<AdminHistorialUiState> = _uiState

    fun onUserIdChange(value: String) {
        _uiState.value = _uiState.value.copy(
            userIdText = value,
            errorMsg = null
        )
    }

    fun buscarHistorial() {
        val text = _uiState.value.userIdText.trim()
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMsg = "Debes ingresar un ID de usuario."
            )
            return
        }

        val userId = text.toLongOrNull()
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                errorMsg = "El ID debe ser un número válido."
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMsg = null
            )

            try {
                val partidas = repo.obtenerHistorial(userId)
                _uiState.value = _uiState.value.copy(
                    partidas = partidas,
                    isLoading = false,
                    errorMsg = if (partidas.isEmpty())
                        "Este usuario no tiene partidas registradas."
                    else null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al cargar historial: ${e.message}"
                )
            }
        }
    }
}

