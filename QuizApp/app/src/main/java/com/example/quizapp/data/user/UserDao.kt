package com.example.quizapp.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    // ---------- REGISTRO ----------
    @Insert
    suspend fun insert(usuario: UserEntity)

    // ---------- OBTENER USUARIO ----------
    @Query("SELECT * FROM Usuario WHERE id_usuario = :id")
    suspend fun getById(id: Int): UserEntity?

    // ---------- CONTAR USUARIOS ----------
    @Query("SELECT COUNT(*) FROM Usuario")
    suspend fun count(): Int

    // ---------- LOGIN ----------
    @Query("SELECT * FROM Usuario WHERE (correo = :input OR nombre = :input) AND clave = :clave")
    suspend fun login(input: String, clave: String): UserEntity?

    // ---------- ACTUALIZAR CONTRASEÑA (para olvidar contraseña) ----------
    @Query("SELECT * FROM Usuario WHERE correo = :identifier OR nombre = :identifier LIMIT 1")
    suspend fun findByEmailOrUsername(identifier: String): UserEntity?

    @Query("UPDATE Usuario SET clave = :nuevaClave WHERE correo = :identifier OR nombre = :identifier")
    suspend fun updatePasswordByIdentifier(identifier: String, nuevaClave: String): Int

    @Query("UPDATE Usuario SET foto_perfil = :foto WHERE id_usuario = :userId")
    fun updateProfilePhoto(userId: Int, foto: ByteArray)


}
