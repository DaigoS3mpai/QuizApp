package com.example.quizapp.data.remote.dto

data class PreguntaAdminResponseDto(
    val id: Long,
    val enunciado: String,
    val categoria: String?,
    val dificultad: String?,
    val estado: String?,
    val opciones: List<OpcionAdminResponseDto>
)

data class OpcionAdminResponseDto(
    val id: Long,
    val texto: String,
    val esCorrecta: Boolean
)
