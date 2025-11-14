package com.example.quizapp.data.rol

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rol")
data class RolEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_rol") val id_rol: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String
)