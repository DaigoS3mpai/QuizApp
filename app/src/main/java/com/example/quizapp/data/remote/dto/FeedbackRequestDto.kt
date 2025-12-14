package com.example.quizapp.data.remote.dto

data class FeedbackRequestDto(
    val usuarioId: Long,
    val mensaje: String,
    val tipo: String,
    val destino: String
)