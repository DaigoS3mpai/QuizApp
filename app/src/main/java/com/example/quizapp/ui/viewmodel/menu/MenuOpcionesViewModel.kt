package com.example.quizapp.ui.viewmodel.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.FunFactApi
import com.example.quizapp.data.remote.FunFactApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FunFactUiState(
    val fact: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class MenuOpcionesViewModel(
    private val api: FunFactApi = FunFactApiClient.api
) : ViewModel() {

    private val _funFactState = MutableStateFlow(FunFactUiState())
    val funFactState: StateFlow<FunFactUiState> = _funFactState

    fun cargarFunFact() {
        viewModelScope.launch {
            _funFactState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = api.getRandomFact()
                _funFactState.update {
                    it.copy(
                        isLoading = false,
                        fact = response.text,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _funFactState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo cargar el dato curioso"
                    )
                }
            }
        }
    }
}
