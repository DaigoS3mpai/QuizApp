package com.example.quizapp.data.dificultad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Dificultad")
data class DificultadEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_dificultad") val id_dificultad: Int = 0,
    @ColumnInfo(name = "nombre_dificultad") val nombre_dificultad: String,
    @ColumnInfo(name = "tiempo_seg") val tiempo_seg: String,
    @ColumnInfo(name = "multip_punt") val multip_punt: Int
)