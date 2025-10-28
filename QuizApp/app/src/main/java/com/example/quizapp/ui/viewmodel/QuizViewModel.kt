package com.example.quizapp.ui.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.database.AppDatabase
import com.example.quizapp.data.opciones.OpcionesEntity
import com.example.quizapp.data.pregunta.PreguntaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class QuizViewModel(private val context: Context) : ViewModel() {

    private val db = AppDatabase.getInstance(context)
    private val preguntaDao = db.preguntaDao()
    private val opcionesDao = db.opcionesDao()
    private val dificultadDao = db.dificultadDao()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    private var preguntasDisponibles: List<PreguntaEntity> = emptyList()
    private var temporizadorJob: Job? = null
    private var tiempoPorPregunta = 30
    private var multiplicadorPuntaje = 1

    fun cargarPreguntas(dificultadId: Int, categoriaId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // 🔹 Cargar configuración de dificultad
            val dificultad = dificultadDao.getById(dificultadId)
            tiempoPorPregunta = dificultad?.tiempo_seg?.toIntOrNull() ?: 30
            multiplicadorPuntaje = dificultad?.multip_punt ?: 1

            // 🔹 Cargar preguntas livianas y convertirlas a PreguntaEntity sin imagen
            var preguntasLite = preguntaDao.getPreguntasLivianasPorDificultadYCategoria(dificultadId, categoriaId)
            var intentos = 0

            while (preguntasLite.isEmpty() && intentos < 3) {
                delay(1000)
                preguntasLite = preguntaDao.getPreguntasLivianasPorDificultadYCategoria(dificultadId, categoriaId)
                intentos++
            }

            // 🔹 Convertir PreguntaLite → PreguntaEntity (imagen vacía por eficiencia)
            preguntasDisponibles = preguntasLite.map { lite ->
                PreguntaEntity(
                    id_pregunta = lite.id_pregunta,
                    imagen = ByteArray(0), // placeholder para evitar carga inicial pesada
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

    private fun mostrarPregunta(index: Int) {
        if (index >= preguntasDisponibles.size) {
            _uiState.update { it.copy(terminado = true) }
            return
        }

        val pregunta = preguntasDisponibles[index]

        viewModelScope.launch(Dispatchers.IO) {
            val opciones = opcionesDao.getOpcionesPorPregunta(pregunta.id_pregunta).shuffled()

            // 🔹 Cargar la imagen real solo cuando sea necesaria
            val imagenBytes = preguntaDao.getImagenPorId(pregunta.id_pregunta)
            val imagenBitmap = imagenBytes?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }

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

    private fun iniciarTemporizador() {
        temporizadorJob?.cancel()
        temporizadorJob = viewModelScope.launch {
            for (i in tiempoPorPregunta downTo 0) {
                delay(1000)
                _uiState.update { it.copy(tiempoRestante = i) }

                if (i == 0) {
                    siguientePregunta()
                }
            }
        }
    }

    fun responder(opcion: OpcionesEntity) {
        val actual = _uiState.value
        if (actual.terminado) return

        val correcta = opcion.correcta == 1
        val nuevoPuntaje =
            if (correcta) actual.puntaje + ((actual.preguntaActual?.puntaje ?: 0) * multiplicadorPuntaje)
            else actual.puntaje

        _uiState.update { it.copy(puntaje = nuevoPuntaje) }

        temporizadorJob?.cancel()
        viewModelScope.launch {
            delay(1000)
            siguientePregunta()
        }
    }

    private fun siguientePregunta() {
        val nextIndex = _uiState.value.preguntaIndex + 1
        mostrarPregunta(nextIndex)
    }
}
