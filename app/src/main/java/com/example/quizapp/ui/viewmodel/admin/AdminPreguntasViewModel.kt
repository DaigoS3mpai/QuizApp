package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.PreguntaAdminResponseDto
import com.example.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminPreguntasUiState(
    val preguntas: List<PreguntaAdminResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class AdminPreguntasViewModel(
    private val repo: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPreguntasUiState())
    val uiState: StateFlow<AdminPreguntasUiState> = _uiState

    init {
        cargarPreguntas()
    }

    fun cargarPreguntas() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null)
            try {
                val lista = repo.obtenerPreguntas()
                _uiState.value = AdminPreguntasUiState(
                    preguntas = lista,
                    isLoading = false,
                    errorMsg = null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al cargar preguntas: ${e.message}"
                )
            }
        }
    }

    fun crearPregunta(
        enunciado: String,
        idCategoria: Long,
        idDificultad: Long,
        idEstado: Long,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ) {
        if (enunciado.isBlank() || textosOpciones.any { it.isBlank() }) {
            _uiState.value = _uiState.value.copy(
                errorMsg = "Debes completar enunciado y todas las opciones."
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.crearPregunta(
                    enunciado = enunciado,
                    idCategoria = idCategoria,
                    idDificultad = idDificultad,
                    idEstado = idEstado,
                    textosOpciones = textosOpciones,
                    indiceCorrecta = indiceCorrecta
                )
                cargarPreguntas()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMsg = "Error al crear pregunta: ${e.message}"
                )
            }
        }
    }

    fun actualizarPregunta(
        id: Long,
        enunciado: String,
        idCategoria: Long,
        idDificultad: Long,
        idEstado: Long,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ) {
        if (enunciado.isBlank() || textosOpciones.any { it.isBlank() }) {
            _uiState.value = _uiState.value.copy(
                errorMsg = "Debes completar enunciado y todas las opciones."
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.actualizarPregunta(
                    id = id,
                    enunciado = enunciado,
                    idCategoria = idCategoria,
                    idDificultad = idDificultad,
                    idEstado = idEstado,
                    textosOpciones = textosOpciones,
                    indiceCorrecta = indiceCorrecta
                )
                cargarPreguntas()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al actualizar pregunta: ${e.message}"
                )
            }
        }
    }


    fun eliminarPregunta(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Llamamos al repositorio correcto
                repo.eliminarPregunta(id)

                // Volvemos a cargar la lista desde el backend
                cargarPreguntas()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al eliminar pregunta: ${e.message}"
                )
            }
        }
    }
}
