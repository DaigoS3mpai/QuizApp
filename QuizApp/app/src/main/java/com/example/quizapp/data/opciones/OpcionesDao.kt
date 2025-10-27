package com.example.quizapp.data.opciones

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OpcionesDao {
    @Insert
    suspend fun insert(opcion: OpcionesEntity)

    @Query("SELECT * FROM Opciones WHERE id_opcion = :id")
    suspend fun getById(id: Int): OpcionesEntity?

    @Query("SELECT COUNT(*) FROM Opciones")
    suspend fun count(): Int
}