package com.example.quizapp.data.pregunta

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quizapp.data.opciones.OpcionesEntity

data class PreguntaConOpciones(
    @Embedded val pregunta: PreguntaEntity,

    @Relation(
        parentColumn = "id_pregunta",
        entityColumn = "id_pregunta_fk"
    )
    val opciones: List<OpcionesEntity>
)
