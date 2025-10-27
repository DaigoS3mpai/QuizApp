package com.example.quizapp.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @Insert
    suspend fun insert(usuario: UserEntity)

    @Query("SELECT * FROM Usuario WHERE id_usuario = :id")
    suspend fun getById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM Usuario")
    suspend fun count(): Int

    @Query("SELECT * FROM Usuario WHERE correo = :correo AND clave = :clave")
    suspend fun login(correo: String, clave: String): UserEntity??
}