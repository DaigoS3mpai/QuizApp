package com.example.quizapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.categoria.CategoriaEntity
import com.example.quizapp.data.database.AppDatabase
import com.example.quizapp.data.dificultad.DificultadEntity
import com.example.quizapp.data.estado.EstadoEntity
import com.example.quizapp.data.opciones.OpcionesEntity
import com.example.quizapp.data.pregunta.PreguntaEntity
import com.example.quizapp.data.pregunta.PreguntaLite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PreguntaDetalleUi(
    val pregunta: PreguntaEntity,
    val opciones: List<OpcionesEntity>
)

class AdminPreguntasViewModel(private val context: Context) : ViewModel() {

    private val db by lazy { AppDatabase.getInstance(context.applicationContext) }
    private val preguntaDao = db.preguntaDao()
    private val opcionesDao = db.opcionesDao()
    private val categoriaDao = db.categoriaDao()
    private val dificultadDao = db.dificultadDao()
    private val estadoDao = db.estadoDao()

    private val _preguntas = MutableStateFlow<List<PreguntaLite>>(emptyList())
    val preguntas: StateFlow<List<PreguntaLite>> = _preguntas

    private val _categorias = MutableStateFlow<List<CategoriaEntity>>(emptyList())
    val categorias: StateFlow<List<CategoriaEntity>> = _categorias

    private val _dificultades = MutableStateFlow<List<DificultadEntity>>(emptyList())
    val dificultades: StateFlow<List<DificultadEntity>> = _dificultades

    private val _estados = MutableStateFlow<List<EstadoEntity>>(emptyList())
    val estados: StateFlow<List<EstadoEntity>> = _estados

    private val _detallePregunta = MutableStateFlow<PreguntaDetalleUi?>(null)
    val detallePregunta: StateFlow<PreguntaDetalleUi?> = _detallePregunta

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listaPreguntas = preguntaDao.getPreguntasLivianas()
                val listaCategorias = categoriaDao.getAll()
                val listaDificultades = dificultadDao.getAll()
                val listaEstados = estadoDao.getAll()

                _preguntas.value = listaPreguntas
                _categorias.value = listaCategorias
                _dificultades.value = listaDificultades
                _estados.value = listaEstados
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun cargarDetallePregunta(idPregunta: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pregunta = preguntaDao.getById(idPregunta)
                if (pregunta != null) {
                    val opciones = opcionesDao.getOpcionesPorPregunta(pregunta.id_pregunta)
                    _detallePregunta.value = PreguntaDetalleUi(pregunta, opciones)
                } else {
                    _detallePregunta.value = null
                }
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun limpiarDetalle() {
        _detallePregunta.value = null
    }

    fun crearPregunta(
        nombre: String,
        puntaje: Int,
        estadoId: Int,
        categoriaId: Int,
        dificultadId: Int,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Por ahora sin imagen -> ByteArray vacío
                val nuevaPregunta = PreguntaEntity(
                    id_pregunta = 0,
                    imagen = ByteArray(0),
                    nombre = nombre,
                    puntaje = puntaje,
                    estado_id_estado = estadoId,
                    categoria_id_categoria = categoriaId,
                    dificultad_id_dificultad = dificultadId
                )

                val idGenerado = preguntaDao.insert(nuevaPregunta).toInt()

                val opciones = textosOpciones.mapIndexed { index, texto ->
                    OpcionesEntity(
                        id_opcion = 0,
                        texto = texto,
                        correcta = if (index == indiceCorrecta) 1 else 0,
                        pregunta_id_pregunta = idGenerado
                    )
                }

                opcionesDao.insertAll(opciones)

                // Recargar lista
                val listaPreguntas = preguntaDao.getPreguntasLivianas()
                _preguntas.value = listaPreguntas

            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun actualizarPregunta(
        idPregunta: Int,
        nombre: String,
        puntaje: Int,
        estadoId: Int,
        categoriaId: Int,
        dificultadId: Int,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existente = preguntaDao.getById(idPregunta) ?: return@launch

                val preguntaActualizada = existente.copy(
                    nombre = nombre,
                    puntaje = puntaje,
                    estado_id_estado = estadoId,
                    categoria_id_categoria = categoriaId,
                    dificultad_id_dificultad = dificultadId
                    // imagen se mantiene igual
                )

                // Actualizar pregunta
                // Usamos insert con mismo id y OnConflict.REPLACE ya configurado en Dao
                preguntaDao.insert(preguntaActualizada)

                // Reemplazar opciones
                opcionesDao.deleteByPreguntaId(idPregunta)
                val opciones = textosOpciones.mapIndexed { index, texto ->
                    OpcionesEntity(
                        id_opcion = 0,
                        texto = texto,
                        correcta = if (index == indiceCorrecta) 1 else 0,
                        pregunta_id_pregunta = idPregunta
                    )
                }
                opcionesDao.insertAll(opciones)

                // Recargar lista
                val listaPreguntas = preguntaDao.getPreguntasLivianas()
                _preguntas.value = listaPreguntas

            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }

    fun eliminarPregunta(idPregunta: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Opciones se borran en cascada por foreign key, pero igual limpiamos
                opcionesDao.deleteByPreguntaId(idPregunta)
                preguntaDao.deleteById(idPregunta)

                val listaPreguntas = preguntaDao.getPreguntasLivianas()
                _preguntas.value = listaPreguntas
            } catch (e: Exception) {
                _mensajeError.value = e.message
            }
        }
    }
}
