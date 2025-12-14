package com.example.quizapp.data.remote.dto

data class UsuarioRequestDto(
    val nombre: String,
    val correo: String,
    val clave: String,
    val idRol: Long,
    val idEstado: Long,
    val puntaje: Int = 0,
    val puntajeGlobal: Int = 0,
    val fotoPerfilBase64: String? = null
)
