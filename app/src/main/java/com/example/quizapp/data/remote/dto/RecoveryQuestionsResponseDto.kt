package com.example.quizapp.data.remote.dto

data class RecoveryQuestionsResponseDto(
    val userId: Long,
    val questions: List<RecoveryQuestionDto>
)
