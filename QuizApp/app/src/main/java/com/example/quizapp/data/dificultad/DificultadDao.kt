package com.example.quizapp.data.dificultad

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DificultadDao {
    @Insert
    suspend fun insert(dificultad: DificultadEntity)

    @Query("SELECT * FROM Dificultad WHERE id_dificultad = :id")
    suspend fun getById(id: Int): DificultadEntity?

    @Query("SELECT COUNT(*) FROM Dificultad")
    suspend fun count(): Int
}