package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.FinalizarPartidaRequestDto
import com.example.quizapp.data.remote.dto.FinalizarPartidaResponseDto
import com.example.quizapp.data.remote.dto.IniciarPartidaRequestDto
import com.example.quizapp.data.remote.dto.IniciarPartidaResponseDto
import com.example.quizapp.data.remote.dto.PartidaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GameApi {

    @POST("api/game/partidas/iniciar")
    suspend fun iniciarPartida(
        @Body body: IniciarPartidaRequestDto
    ): IniciarPartidaResponseDto

    @POST("api/game/partidas/finalizar")
    suspend fun finalizarPartida(
        @Body body: FinalizarPartidaRequestDto
    ): FinalizarPartidaResponseDto

    @GET("api/game/partidas/usuario/{usuarioId}")
    suspend fun obtenerPartidasPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<PartidaDto>
}
