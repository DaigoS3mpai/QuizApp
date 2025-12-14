package com.example.quizapp.data.remote.dto

data class LoginRequestDto(
    val identificador: String, // correo o nombre de usuario
    val clave: String          // contraseña en texto plano
)