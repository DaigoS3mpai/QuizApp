package com.example.quizapp.data.remote.dto

data class PreguntaDto(
    val id: Long,
    val enunciado: String,

    // Cuando vienen de quiz-service:
    val categoria: String? = null,
    val dificultad: String? = null,
    val estado: String? = null,

    // Cuando vienen de game-service:
    val puntaje: Int? = null,

    val opciones: List<OpcionDto>
)
