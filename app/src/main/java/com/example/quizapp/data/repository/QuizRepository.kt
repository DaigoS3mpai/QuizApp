package com.example.quizapp.data.repository

import com.example.quizapp.data.remote.ApiClient
import com.example.quizapp.data.remote.dto.OpcionAdminRequestDto
import com.example.quizapp.data.remote.dto.PreguntaAdminRequestDto
import com.example.quizapp.data.remote.dto.PreguntaAdminResponseDto

class QuizRepository {

    private val api = ApiClient.quizApi

    suspend fun obtenerPreguntas(): List<PreguntaAdminResponseDto> =
        api.obtenerPreguntas()

    suspend fun crearPregunta(
        enunciado: String,
        idCategoria: Long,
        idDificultad: Long,
        idEstado: Long,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ): PreguntaAdminResponseDto {

        val opciones = textosOpciones.mapIndexed { index, texto ->
            OpcionAdminRequestDto(
                texto = texto,
                esCorrecta = index == indiceCorrecta
            )
        }

        val body = PreguntaAdminRequestDto(
            enunciado = enunciado,
            idCategoria = idCategoria,
            idDificultad = idDificultad,
            idEstado = idEstado,
            opciones = opciones
        )

        return api.crearPregunta(body)
    }

    suspend fun actualizarPregunta(
        id: Long,
        enunciado: String,
        idCategoria: Long,
        idDificultad: Long,
        idEstado: Long,
        textosOpciones: List<String>,
        indiceCorrecta: Int
    ): PreguntaAdminResponseDto {

        val opciones = textosOpciones.mapIndexed { index, texto ->
            OpcionAdminRequestDto(
                texto = texto,
                esCorrecta = index == indiceCorrecta
            )
        }

        val body = PreguntaAdminRequestDto(
            enunciado = enunciado,
            idCategoria = idCategoria,
            idDificultad = idDificultad,
            idEstado = idEstado,
            opciones = opciones
        )

        return api.actualizarPregunta(id, body)
    }


    suspend fun eliminarPregunta(id: Long) {
        val response = api.eliminarPregunta(id)

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}")
        }
    }
}
