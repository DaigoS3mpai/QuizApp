package com.example.quizapp.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import com.example.quizapp.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminJugadoresViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<UsuarioResponseDto>>(emptyList())
    val usuarios: StateFlow<List<UsuarioResponseDto>> = _usuarios

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lista = repo.obtenerTodosLosUsuarios()
                _usuarios.value = lista
                _error.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al cargar usuarios: ${e.message}"
            }
        }
    }

    fun eliminarJugador(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.eliminarUsuario(id)
                cargarUsuarios()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al eliminar usuario: ${e.message}"
            }
        }
    }

    fun actualizarPuntaje(id: Long, puntaje: Int, puntajeGlobal: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.actualizarPuntajesAdmin(id, puntaje, puntajeGlobal)
                cargarUsuarios()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error al actualizar puntajes: ${e.message}"
            }
        }
    }
}
