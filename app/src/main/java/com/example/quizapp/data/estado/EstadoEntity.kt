package com.example.quizapp.data.estado

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Estado")
data class EstadoEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_estado") val id_estado: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String
)