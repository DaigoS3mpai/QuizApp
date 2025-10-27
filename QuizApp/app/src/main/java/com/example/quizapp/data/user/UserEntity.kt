package com.example.quizapp.data.user
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.quizapp.data.estado.EstadoEntity
import com.example.quizapp.data.rol.RolEntity

@Entity(
    tableName = "Usuario",
    foreignKeys = [
        ForeignKey(
            entity = RolEntity::class,
            parentColumns = ["id_rol"],
            childColumns = ["Rol_id_rol"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EstadoEntity::class,
            parentColumns = ["id_estado"],
            childColumns = ["Estado_id_estado"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_usuario") val id_usuario: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "clave") val clave: String,
    @ColumnInfo(name = "foto_perfil") val foto_perfil: ByteArray,
    @ColumnInfo(name = "Rol_id_rol") val rol_id_rol: Int,
    @ColumnInfo(name = "Estado_id_estado") val estado_id_estado: Int
)