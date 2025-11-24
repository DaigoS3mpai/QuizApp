package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.FeedbackRequestDto
import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FeedbackApi {

    // POST /api/feedback  -> crear feedback
    @POST("api/feedback")
    suspend fun crearFeedback(
        @Body request: FeedbackRequestDto
    ): FeedbackResponseDto

    // GET /api/feedback   -> listar TODOS los feedback
    @GET("api/feedback")
    suspend fun listarFeedback(): List<FeedbackResponseDto>

    // GET /api/feedback/pendientes -> sólo pendientes (para admin)
    @GET("api/feedback/pendientes")
    suspend fun listarPendientes(): List<FeedbackResponseDto>

    // GET /api/feedback/usuario/{usuarioId} -> feedback de un usuario
    @GET("api/feedback/usuario/{usuarioId}")
    suspend fun listarPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<FeedbackResponseDto>

    // PATCH /api/feedback/{id}/resolver -> marcar como resuelto
    @PATCH("api/feedback/{id}/resolver")
    suspend fun resolver(
        @Path("id") id: Long
    ): FeedbackResponseDto
}
