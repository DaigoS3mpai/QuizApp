package com.example.quizapp.data.remote

import com.example.quizapp.data.remote.dto.ActualizarFotoPerfilRequestDto
import com.example.quizapp.data.remote.dto.LoginRequestDto
import com.example.quizapp.data.remote.dto.RecoveryQuestionsResponseDto
import com.example.quizapp.data.remote.dto.ResetPasswordRequestDto
import com.example.quizapp.data.remote.dto.SecurityQuestionDto
import com.example.quizapp.data.remote.dto.SetupSecurityQuestionsRequestDto
import com.example.quizapp.data.remote.dto.UsuarioRequestDto
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import com.example.quizapp.data.remote.dto.VerifyRecoveryRequestDto
import com.example.quizapp.data.remote.dto.VerifyRecoveryResponseDto
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

    @PATCH("api/auth/usuarios/{id}/foto-perfil")
    suspend fun actualizarFotoPerfil(
        @Path("id") id: Long,
        @Body body: ActualizarFotoPerfilRequestDto
    ): Response<Unit>

    @POST("api/auth/usuarios/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): UsuarioResponseDto

    @PUT("api/auth/usuarios/password")
    suspend fun actualizarPasswordPorCorreo(
        @Query("identificador") identificador: String,
        @Query("nuevaClave") nuevaClave: String
    ): Response<Unit>

    @GET("api/auth/password-recovery/questions")
    suspend fun getRecoveryQuestions(
        @Query("identificador") identificador: String
    ): RecoveryQuestionsResponseDto

    @POST("api/auth/password-recovery/verify")
    suspend fun verifyRecovery(
        @Body body: VerifyRecoveryRequestDto
    ): VerifyRecoveryResponseDto

    @POST("api/auth/password-recovery/reset")
    suspend fun resetPasswordRecovery(
        @Body body: ResetPasswordRequestDto
    ): Response<Unit>

    @GET("api/auth/security-questions")
    suspend fun getSecurityQuestions(): List<SecurityQuestionDto>

    @POST("api/auth/security-questions/setup")
    suspend fun setupSecurityQuestions(
        @Body body: SetupSecurityQuestionsRequestDto
    ): Response<Unit>

}
