package com.example.quizapp.data.remote.dto

data class UsuarioResponseDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String?,
    val estado: String?,
    val puntaje: Int,
    val puntajeGlobal: Int
)
