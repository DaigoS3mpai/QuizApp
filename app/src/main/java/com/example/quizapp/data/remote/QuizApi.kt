package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.PreguntaAdminRequestDto
import com.example.quizapp.data.remote.dto.PreguntaAdminResponseDto
import retrofit2.http.*


interface QuizApi {

    // GET /api/quiz/preguntas
    @GET("api/quiz/preguntas")
    suspend fun obtenerPreguntas(): List<PreguntaAdminResponseDto>

    // POST /api/quiz/preguntas
    @POST("api/quiz/preguntas")
    suspend fun crearPregunta(
        @Body request: PreguntaAdminRequestDto
    ): PreguntaAdminResponseDto

    // DELETE /api/quiz/preguntas/{id}
    @DELETE("api/quiz/preguntas/{id}")
    suspend fun eliminarPregunta(
        @Path("id") id: Long
    ): retrofit2.Response<Unit>

    @PUT("api/quiz/preguntas/{id}")
    suspend fun actualizarPregunta(
        @Path("id") id: Long,
        @Body request: PreguntaAdminRequestDto
    ): PreguntaAdminResponseDto

}
