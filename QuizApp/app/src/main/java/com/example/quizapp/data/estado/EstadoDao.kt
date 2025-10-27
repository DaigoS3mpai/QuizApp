package com.example.quizapp.data.estado

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface EstadoDao {
    @Insert
    suspend fun insert(estado: EstadoEntity)

    @Query("SELECT * FROM Estado WHERE id_estado = :id")
    suspend fun getById(id: Int): EstadoEntity?

    @Query("SELECT COUNT(*) FROM Estado")
    suspend fun count(): Int
}