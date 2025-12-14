package com.example.quizapp.data.repository

import com.example.quizapp.data.remote.ApiClient
import com.example.quizapp.data.remote.dto.*

class GameRepository {

    private val api = ApiClient.gameApi

    suspend fun iniciarPartida(
        usuarioId: Long,
        categoriaId: Long,
        dificultadId: Long
    ): IniciarPartidaResponseDto {
        val body = IniciarPartidaRequestDto(
            usuarioId = usuarioId,
            categoriaId = categoriaId,
            dificultadId = dificultadId
        )
        return api.iniciarPartida(body)
    }

    suspend fun finalizarPartida(
        partidaId: Long,
        puntajeObtenido: Int
    ): FinalizarPartidaResponseDto {
        val body = FinalizarPartidaRequestDto(
            partidaId = partidaId,
            puntajeObtenido = puntajeObtenido
        )
        return api.finalizarPartida(body)
    }

    suspend fun obtenerHistorial(usuarioId: Long): List<PartidaDto> =
        api.obtenerPartidasPorUsuario(usuarioId)
}
