package com.example.quizapp.data.categoria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categoria")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_categoria") val id_categoria: Int = 0,
    @ColumnInfo(name = "nombre_categoria") val nombre_categoria: String
)