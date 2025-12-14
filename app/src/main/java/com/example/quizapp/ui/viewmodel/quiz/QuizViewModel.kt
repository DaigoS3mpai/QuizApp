package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.dto.OpcionDto
import com.example.quizapp.data.remote.dto.PreguntaDto
import com.example.quizapp.data.repository.GameRepository
import com.example.quizapp.utils.sessionDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ----------------- UI STATE -----------------
data class QuizUiState(
    val preguntaActual: PreguntaDto? = null,
    val opciones: List<OpcionDto> = emptyList(),
    val tiempoRestante: Int = 0,
    val puntaje: Int = 0,
    val preguntaIndex: Int = 0,
    val totalPreguntas: Int = 0,
    val terminado: Boolean = false
)

// ----------------- VIEWMODEL -----------------
class QuizViewModel(private val context: Context) : ViewModel() {

    private val gameRepository = GameRepository()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    // Mostrar “Perdiste”
    private val _mostrarPerdiste = MutableStateFlow(false)
    val mostrarPerdiste: StateFlow<Boolean> = _mostrarPerdiste

    private var preguntasRemotas: List<PreguntaDto> = emptyList()
    private var temporizadorJob: Job? = null

    // ⏱️ Estos ahora se configuran según la dificultad
    private var tiempoPorPregunta = 30           // valor por defecto
    private var multiplicadorPuntaje = 1         // valor por defecto

    private var currentUserId: Long? = null

    private var categoriaActualId: Long = 0
    private var dificultadActualId: Long = 0

    private var fechaInicioPartida: String = ""
    private var partidaRemotaId: Long? = null
    private var partidaFinalizada: Boolean = false

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }

    init {
        // Cargar ID de usuario desde DataStore
        viewModelScope.launch(Dispatchers.IO) {
            val id = context.sessionDataStore.data.map { it[USER_ID_KEY] ?: -1 }.first()
            if (id != -1) currentUserId = id.toLong()
        }
    }

    // ----------------- INICIAR QUIZ (game-service) -----------------
    fun iniciarQuiz(dificultadId: Long, categoriaId: Long) {
        dificultadActualId = dificultadId
        categoriaActualId = categoriaId
        partidaFinalizada = false
        partidaRemotaId = null

        // ⏱️ Configurar tiempo y multiplicador según la dificultad
        when (dificultadId) {
            1L -> { // Fácil
                tiempoPorPregunta = 30
                multiplicadorPuntaje = 1
            }
            2L -> { // Normal
                tiempoPorPregunta = 20
                multiplicadorPuntaje = 2
            }
            3L -> { // Difícil
                tiempoPorPregunta = 10
                multiplicadorPuntaje = 3
            }
            else -> {
                tiempoPorPregunta = 30
                multiplicadorPuntaje = 1
            }
        }

        // Fecha local de respaldo
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        fechaInicioPartida = dateFormat.format(Date())

        viewModelScope.launch(Dispatchers.IO) {
            _mostrarPerdiste.value = false

            val userId = currentUserId
            if (userId != null) {
                try {
                    // Llamada al microservicio game-service
                    val resp = gameRepository.iniciarPartida(
                        usuarioId = userId,
                        categoriaId = categoriaId,
                        dificultadId = dificultadId
                    )

                    partidaRemotaId = resp.partidaId
                    fechaInicioPartida = resp.fechaInicio.replace("T", " ")
                    preguntasRemotas = resp.preguntas.shuffled()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Si falla, el quiz no puede continuar sin preguntas
                    preguntasRemotas = emptyList()
                }
            } else {
                // No hay usuario logueado, no podemos iniciar partida
                preguntasRemotas = emptyList()
            }

            if (preguntasRemotas.isNotEmpty()) {
                mostrarPregunta(0)
            } else {
                _uiState.update { it.copy(terminado = true) }
            }
        }
    }

    // ----------------- MOSTRAR PREGUNTA -----------------
    private fun mostrarPregunta(index: Int) {
        if (index >= preguntasRemotas.size) {
            finalizarPartidaRemota()
            _uiState.update { it.copy(terminado = true) }
            return
        }

        val pregunta = preguntasRemotas[index]

        // 🔹 Mezclamos el orden de las opciones de esta pregunta
        val opcionesMezcladas = pregunta.opciones.shuffled()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    preguntaActual = pregunta,          // la pregunta original
                    opciones = opcionesMezcladas,       // opciones en orden aleatorio
                    tiempoRestante = tiempoPorPregunta,
                    preguntaIndex = index,
                    totalPreguntas = preguntasRemotas.size,
                    terminado = false
                )
            }

            iniciarTemporizador()
        }
    }



    // ----------------- TEMPORIZADOR -----------------
    private fun iniciarTemporizador() {
        temporizadorJob?.cancel()
        temporizadorJob = viewModelScope.launch {
            for (i in tiempoPorPregunta downTo 0) {
                delay(1000)
                _uiState.update { it.copy(tiempoRestante = i) }

                if (i == 0) {
                    withContext(Dispatchers.Main) {
                        _mostrarPerdiste.value = true
                    }
                    finalizarPartidaRemota()
                    _uiState.update { it.copy(terminado = true) }
                    return@launch
                }
            }
        }
    }

    // ----------------- RESPONDER -----------------
    fun responder(opcion: OpcionDto) {
        val actual = _uiState.value
        if (actual.terminado) return

        val esCorrecta = opcion.correcta

        // 🧮 Puntaje:
        // - Usa el puntaje de la pregunta si viene del backend
        // - Si viene null, tomamos 10 como base
        val puntosBasePregunta = actual.preguntaActual?.puntaje ?: 10
        val puntosGanados = if (esCorrecta) puntosBasePregunta * multiplicadorPuntaje else 0

        val nuevoPuntaje = actual.puntaje + puntosGanados

        _uiState.update { it.copy(puntaje = nuevoPuntaje) }

        temporizadorJob?.cancel()

        if (!esCorrecta) {
            // Si se equivoca, pierde
            viewModelScope.launch(Dispatchers.Main) {
                _mostrarPerdiste.value = true
            }
            finalizarPartidaRemota()
            _uiState.update { it.copy(terminado = true) }
            return
        }

        // Si acierta, pasa a la siguiente
        viewModelScope.launch {
            delay(1000)
            siguientePregunta()
        }
    }

    // ----------------- SIGUIENTE PREGUNTA -----------------
    private fun siguientePregunta() {
        val nextIndex = _uiState.value.preguntaIndex + 1
        if (nextIndex >= preguntasRemotas.size) {
            finalizarPartidaRemota()
            _uiState.update { it.copy(terminado = true) }
        } else {
            mostrarPregunta(nextIndex)
        }
    }

    // ----------------- FINALIZAR PARTIDA EN GAME-SERVICE -----------------
    private fun finalizarPartidaRemota() {
        if (partidaFinalizada) return
        partidaFinalizada = true

        val partidaId = partidaRemotaId ?: return
        val puntajeObtenido = _uiState.value.puntaje

        viewModelScope.launch(Dispatchers.IO) {
            try {
                gameRepository.finalizarPartida(
                    partidaId = partidaId,
                    puntajeObtenido = puntajeObtenido
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla, no hacemos nada más por ahora
            } finally {
                partidaRemotaId = null
            }
        }
    }
}
