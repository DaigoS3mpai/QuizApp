package com.example.quizapp.data.remote.dto

data class SetupSecurityQuestionsRequestDto(
    val userId: Long,
    val items: List<Item>
) {
    data class Item(
        val preguntaId: Int,
        val respuesta: String
    )
}