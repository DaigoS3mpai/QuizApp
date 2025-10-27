package com.example.quizapp.data.pregunta
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.quizapp.data.categoria.CategoriaEntity
import com.example.quizapp.data.dificultad.DificultadEntity
import com.example.quizapp.data.estado.EstadoEntity

@Entity(
    tableName = "Pregunta",
    foreignKeys = [
        ForeignKey(
            entity = EstadoEntity::class,
            parentColumns = ["id_estado"],
            childColumns = ["Estado_id_estado"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id_categoria"],
            childColumns = ["Categoria_id_categoria"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DificultadEntity::class,
            parentColumns = ["id_dificultad"],
            childColumns = ["Dificultad_id_dificultad"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PreguntaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_pregunta") val id_pregunta: Int = 0,
    @ColumnInfo(name = "imagen") val imagen: ByteArray,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "puntaje") val puntaje: Int,
    @ColumnInfo(name = "Estado_id_estado") val estado_id_estado: Int,
    @ColumnInfo(name = "Categoria_id_categoria") val categoria_id_categoria: Int,
    @ColumnInfo(name = "Dificultad_id_dificultad") val dificultad_id_dificultad: Int
)