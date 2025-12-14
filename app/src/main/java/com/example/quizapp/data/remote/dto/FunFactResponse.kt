package com.example.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FunFactResponse(
    val id: String?,
    @SerializedName("text")
    val text: String
)
