package com.example.quizapp.data.remote.dto

data class PreguntaAdminRequestDto(
    val enunciado: String,
    val idCategoria: Long,
    val idDificultad: Long,
    val idEstado: Long,
    val opciones: List<OpcionAdminRequestDto>
)

data class OpcionAdminRequestDto(
    val texto: String,
    val esCorrecta: Boolean
)
