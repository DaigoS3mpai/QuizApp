package com.example.quizapp.data.remote.dto

data class PartidaDto(
    val id: Long,
    val usuarioId: Long,
    val categoria: String,
    val dificultad: String,
    val fechaInicio: String,
    val fechaFin: String?,
    val puntajeFinal: Int,
    val estado: String
)
