package com.example.quizapp.data.partida

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.quizapp.data.categoria.CategoriaEntity
import com.example.quizapp.data.dificultad.DificultadEntity
import com.example.quizapp.data.estado.EstadoEntity
import com.example.quizapp.data.user.UserEntity

@Entity(
    tableName = "Partida",
    foreignKeys = [
        ForeignKey(
            entity = EstadoEntity::class,
            parentColumns = ["id_estado"],
            childColumns = ["Estado_id_estado"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["Usuario_id_usuario"],
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
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_partida") val id_partida: Int = 0,
    @ColumnInfo(name = "f_creacion") val f_creacion: String,
    @ColumnInfo(name = "f_finalizacion") val f_finalizacion: String?,
    @ColumnInfo(name = "punt_total") val punt_total: Int,
    @ColumnInfo(name = "Estado_id_estado") val estado_id_estado: Int,
    @ColumnInfo(name = "Usuario_id_usuario") val usuario_id_usuario: Int,
    @ColumnInfo(name = "Categoria_id_categoria") val categoria_id_categoria: Int,
    @ColumnInfo(name = "Dificultad_id_dificultad") val dificultad_id_dificultad: Int
)