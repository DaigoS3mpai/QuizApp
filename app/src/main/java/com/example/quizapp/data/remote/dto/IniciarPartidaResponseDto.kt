package com.example.quizapp.data.remote.dto

data class IniciarPartidaResponseDto(
    val partidaId: Long,
    val fechaInicio: String,
    val preguntas: List<PreguntaDto>
)
