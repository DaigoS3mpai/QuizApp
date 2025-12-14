package com.example.quizapp.data.remote.dto

data class FeedbackResponseDto(
    val id: Long,
    val mensaje: String,
    val tipo: String,
    val destino: String,
    val fecha: String,      // LocalDateTime en backend → aquí como String
    val resuelto: Boolean,
    val usuarioId: Long
)