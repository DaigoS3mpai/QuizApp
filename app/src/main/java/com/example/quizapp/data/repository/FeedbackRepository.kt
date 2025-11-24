package com.example.quizapp.data.repository

import com.example.quizapp.data.remote.ApiClient
import com.example.quizapp.data.remote.dto.FeedbackRequestDto
import com.example.quizapp.data.remote.dto.FeedbackResponseDto

class FeedbackRepository {

    private val api = ApiClient.feedbackApi

    // Jugador envía feedback
    suspend fun enviarFeedback(
        usuarioId: Long,
        mensaje: String,
        tipo: String,
        destino: String
    ): FeedbackResponseDto {
        val body = FeedbackRequestDto(
            usuarioId = usuarioId,
            mensaje = mensaje,
            tipo = tipo,
            destino = destino
        )
        return api.crearFeedback(body)
    }

    // Admin: listar todos
    suspend fun listarTodos(): List<FeedbackResponseDto> =
        api.listarFeedback()

    // Admin: listar sólo pendientes
    suspend fun listarPendientes(): List<FeedbackResponseDto> =
        api.listarPendientes()

    // Ver feedback por usuario (lo podemos usar luego en la pantalla de jugador)
    suspend fun listarPorUsuario(usuarioId: Long): List<FeedbackResponseDto> =
        api.listarPorUsuario(usuarioId)

    // Admin: marcar como resuelto
    suspend fun resolver(id: Long): FeedbackResponseDto =
        api.resolver(id)
}
