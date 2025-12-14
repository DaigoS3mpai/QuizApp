package com.example.quizapp.data.remote.dto

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)