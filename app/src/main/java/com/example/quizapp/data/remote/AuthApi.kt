package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.UsuarioRequestDto
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @GET("api/auth/usuarios")
    suspend fun getUsuarios(): List<UsuarioResponseDto>

    @GET("api/auth/usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Long): UsuarioResponseDto

    @POST("api/auth/usuarios")
    suspend fun crearUsuario(@Body body: UsuarioRequestDto): UsuarioResponseDto

    @PUT("api/auth/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body body: UsuarioRequestDto
    ): UsuarioResponseDto

    @DELETE("api/auth/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Path("id") id: Long
    ): Response<Unit>
}
