package com.example.quizapp.data.feedback

import androidx.room.ColumnInfo

data class FeedbackConUsuario(
    @ColumnInfo(name = "id_feedback") val id_feedback: Int,
    @ColumnInfo(name = "mensaje") val mensaje: String,
    @ColumnInfo(name = "tipo") val tipo: String,
    @ColumnInfo(name = "destino") val destino: String,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "resuelto") val resuelto: Int,
    @ColumnInfo(name = "Usuario_id_usuario") val usuario_id_usuario: Int,
    @ColumnInfo(name = "nombre_usuario") val nombre_usuario: String
)
