package com.example.quizapp.ui.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.database.AppDatabase
import com.example.quizapp.data.opciones.OpcionesEntity
import com.example.quizapp.data.pregunta.PreguntaEntity
import com.example.quizapp.data.user.UserDao
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
import androidx.datastore.preferences.core.intPreferencesKey

// ----------------- UI STATE -----------------
data class QuizUiState(
    val preguntaActual: PreguntaEntity? = null,
    val opciones: List<OpcionesEntity> = emptyList(),
    val imagenBitmap: android.graphics.Bitmap? = null,
    val tiempoRestante: Int = 0,
    val puntaje: Int = 0,
    val preguntaIndex: Int = 0,
    val totalPreguntas: Int = 0,
    val terminado: Boolean = false
)

// ----------------- VIEWMODEL -----------------
class QuizViewModel(private val context: Context) : ViewModel() {

    private val db by lazy { AppDatabase.getInstance(context.applicationContext) }
    private val preguntaDao = db.preguntaDao()
    private val opcionesDao = db.opcionesDao()
    private val dificultadDao = db.dificultadDao()
    private val userDao: UserDao = db.usuarioDao()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    // 🔹 Nuevo: evento para mostrar “Perdiste”
    private val _mostrarPerdiste = MutableStateFlow(false)
    val mostrarPerdiste: StateFlow<Boolean> = _mostrarPerdiste

    private var preguntasDisponibles: List<PreguntaEntity> = emptyList()
    private var temporizadorJob: Job? = null
    private var tiempoPorPregunta = 30
    private var multiplicadorPuntaje = 1
    private var currentUserId: Int? = null

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }

    init {
        // ✅ Cargar el ID del usuario actual desde DataStore global
        viewModelScope.launch(Dispatchers.IO) {
            val id = context.sessionDataStore.data.map { it[USER_ID_KEY] ?: -1 }.first()
            if (id != -1) currentUserId = id
        }
    }

    // ----------------- CARGAR PREGUNTAS -----------------
    fun cargarPreguntas(dificultadId: Int, categoriaId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _mostrarPerdiste.value = false // reiniciar estado al empezar quiz

            val dificultad = dificultadDao.getById(dificultadId)
            tiempoPorPregunta = dificultad?.tiempo_seg?.toIntOrNull() ?: 30
            multiplicadorPuntaje = dificultad?.multip_punt ?: 1

            var preguntasLite =
                preguntaDao.getPreguntasLivianasPorDificultadYCategoria(dificultadId, categoriaId)
            var intentos = 0

            while (preguntasLite.isEmpty() && intentos < 3) {
                delay(1000)
                preguntasLite =
                    preguntaDao.getPreguntasLivianasPorDificultadYCategoria(dificultadId, categoriaId)
                intentos++
            }

            preguntasDisponibles = preguntasLite.map { lite ->
                PreguntaEntity(
                    id_pregunta = lite.id_pregunta,
                    imagen = ByteArray(0),
                    nombre = lite.nombre,
                    puntaje = lite.puntaje,
                    estado_id_estado = lite.estado_id_estado,
                    categoria_id_categoria = lite.categoria_id_categoria,
                    dificultad_id_dificultad = lite.dificultad_id_dificultad
                )
            }.shuffled()

            if (preguntasDisponibles.isNotEmpty()) {
                mostrarPregunta(0)
            } else {
                _uiState.update { it.copy(terminado = true) }
            }
        }
    }

    // ----------------- MOSTRAR PREGUNTA -----------------
    private fun mostrarPregunta(index: Int) {
        if (index >= preguntasDisponibles.size) {
            guardarPuntajeEnPerfil(_uiState.value.puntaje)
            _uiState.update { it.copy(terminado = true) }
            return
        }

        val pregunta = preguntasDisponibles[index]

        viewModelScope.launch(Dispatchers.IO) {
            val opciones = opcionesDao.getOpcionesPorPregunta(pregunta.id_pregunta).shuffled()
            val imagenBytes = preguntaDao.getImagenPorId(pregunta.id_pregunta)
            val imagenBitmap = imagenBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

            _uiState.update {
                it.copy(
                    preguntaActual = pregunta,
                    opciones = opciones,
                    imagenBitmap = imagenBitmap,
                    tiempoRestante = tiempoPorPregunta,
                    preguntaIndex = index,
                    totalPreguntas = preguntasDisponibles.size,
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
                    // 🕒 Tiempo agotado → mostrar mensaje y terminar quiz
                    withContext(Dispatchers.Main) {
                        _mostrarPerdiste.value = true
                    }
                    guardarPuntajeEnPerfil(_uiState.value.puntaje)
                    _uiState.update { it.copy(terminado = true) }
                    return@launch
                }
            }
        }
    }

    // ----------------- RESPONDER -----------------
    fun responder(opcion: OpcionesEntity) {
        val actual = _uiState.value
        if (actual.terminado) return

        val correcta = opcion.correcta == 1
        val nuevoPuntaje =
            if (correcta)
                actual.puntaje + ((actual.preguntaActual?.puntaje ?: 0) * multiplicadorPuntaje)
            else actual.puntaje

        _uiState.update { it.copy(puntaje = nuevoPuntaje) }

        temporizadorJob?.cancel()

        // Si se equivoca, mostrar también “Perdiste”
        if (!correcta) {
            viewModelScope.launch(Dispatchers.Main) {
                _mostrarPerdiste.value = true
            }
            guardarPuntajeEnPerfil(nuevoPuntaje)
            _uiState.update { it.copy(terminado = true) }
            return
        }

        // Si acierta, pasar a la siguiente
        viewModelScope.launch {
            delay(1000)
            siguientePregunta()
        }
    }

    // ----------------- SIGUIENTE PREGUNTA -----------------
    private fun siguientePregunta() {
        val nextIndex = _uiState.value.preguntaIndex + 1
        if (nextIndex >= preguntasDisponibles.size) {
            guardarPuntajeEnPerfil(_uiState.value.puntaje)
            _uiState.update { it.copy(terminado = true) }
        } else {
            mostrarPregunta(nextIndex)
        }
    }

    // ----------------- GUARDAR PUNTAJE EN PERFIL -----------------
    private fun guardarPuntajeEnPerfil(puntajeObtenido: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId?.let { userId ->
                userDao.agregarPuntaje(userId, puntajeObtenido)
                userDao.agregarPuntajeGlobal(userId, puntajeObtenido)
            }
        }
    }
}
