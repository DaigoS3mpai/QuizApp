package com.example.quizapp.data.pregunta

import androidx.room.ColumnInfo

data class PreguntaLite(
    @ColumnInfo(name = "id_pregunta") val id_pregunta: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "puntaje") val puntaje: Int,
    @ColumnInfo(name = "Estado_id_estado") val estado_id_estado: Int,
    @ColumnInfo(name = "Categoria_id_categoria") val categoria_id_categoria: Int,
    @ColumnInfo(name = "Dificultad_id_dificultad") val dificultad_id_dificultad: Int
)
