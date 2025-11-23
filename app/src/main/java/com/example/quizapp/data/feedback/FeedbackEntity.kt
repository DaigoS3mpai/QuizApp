package com.example.quizapp.data.feedback

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.quizapp.data.user.UserEntity

@Entity(
    tableName = "Feedback",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["Usuario_id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("Usuario_id_usuario")
    ]
)
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_feedback")
    val id_feedback: Int = 0,

    @ColumnInfo(name = "mensaje")
    val mensaje: String,

    // "PREGUNTA", "PUNTAJE", "OTRO"
    @ColumnInfo(name = "tipo")
    val tipo: String,

    // "QUIZ", "ADMIN", "AMBOS"
    @ColumnInfo(name = "destino")
    val destino: String,

    @ColumnInfo(name = "fecha")
    val fecha: String,

    // 0 = pendiente, 1 = resuelto
    @ColumnInfo(name = "resuelto")
    val resuelto: Int = 0,

    @ColumnInfo(name = "Usuario_id_usuario")
    val usuario_id_usuario: Int
)
