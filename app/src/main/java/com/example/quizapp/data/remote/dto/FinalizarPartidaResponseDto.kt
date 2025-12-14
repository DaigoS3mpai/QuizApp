package com.example.quizapp.data.remote.dto

data class FinalizarPartidaResponseDto(
    val partidaId: Long,
    val puntajeObtenido: Int,
    val puntajeAnteriorGlobal: Int,
    val puntajeNuevoGlobal: Int,
    val fechaFin: String,
    val mensaje: String
)
