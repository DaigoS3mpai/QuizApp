package com.example.quizapp.data.opciones

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.quizapp.data.pregunta.PreguntaEntity

@Entity(
    tableName = "Opciones",
    foreignKeys = [
        ForeignKey(
            entity = PreguntaEntity::class,
            parentColumns = ["id_pregunta"],
            childColumns = ["Pregunta_id_pregunta"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OpcionesEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_opcion") val id_opcion: Int = 0,
    @ColumnInfo(name = "texto") val texto: String,
    @ColumnInfo(name = "correcta") val correcta: Int,
    @ColumnInfo(name = "Pregunta_id_pregunta") val pregunta_id_pregunta: Int
)