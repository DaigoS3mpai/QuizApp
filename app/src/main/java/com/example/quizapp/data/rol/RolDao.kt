package com.example.quizapp.data.rol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RolDao {
    @Insert
    suspend fun insert(rol: RolEntity)

    @Query("SELECT * FROM Rol WHERE id_rol = :id")
    suspend fun getById(id: Int): RolEntity?

    @Query("SELECT COUNT(*) FROM Rol")
    suspend fun count(): Int

    @Query("SELECT * FROM Rol")
    suspend fun getAll(): List<RolEntity>
}
