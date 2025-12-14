package com.example.quizapp.ui.viewmodel.Auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PartidaUsuarioUi(
    val id: Long,
    val categoria: String,
    val dificultad: String,
    val fechaInicio: String,
    val fechaFin: String?,
    val puntaje: Int,
    val estado: String
)

data class PartidasUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val partidas: List<PartidaUsuarioUi> = emptyList()
)

class PartidasViewModel(
    private val gameRepository: GameRepository = GameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartidasUiState())
    val uiState: StateFlow<PartidasUiState> = _uiState

    fun cargarPartidasUsuario(usuarioId: Long) {
        if (usuarioId <= 0) {
            Log.e("PartidasViewModel", "❌ usuarioId inválido ($usuarioId)")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                Log.d("PartidasViewModel", "🔵 Cargando partidas de usuario $usuarioId...")
                val partidasDto = gameRepository.obtenerHistorial(usuarioId)

                val partidasFiltradas = partidasDto.filter { dto ->
                    dto.usuarioId == usuarioId
                }

                Log.d(
                    "PartidasViewModel",
                    "📥 DTOs recibidos: ${partidasDto.size}, tras filtro por usuarioId=$usuarioId -> ${partidasFiltradas.size}"
                )

                val partidasUi = partidasFiltradas.map { dto ->
                    PartidaUsuarioUi(
                        id = dto.id,
                        categoria = dto.categoria,
                        dificultad = dto.dificultad,
                        fechaInicio = dto.fechaInicio,
                        fechaFin = dto.fechaFin,
                        puntaje = dto.puntajeFinal,
                        estado = dto.estado
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        partidas = partidasUi,
                        error = null
                    )
                }

            } catch (e: Exception) {
                Log.e("PartidasViewModel", "❌ Error cargando historial", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar partidas: ${e.message}",
                        partidas = emptyList()
                    )
                }
            }
        }
    }
}
