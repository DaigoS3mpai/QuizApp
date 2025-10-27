package com.example.quizapp.data.pregunta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PreguntaDao {
    @Insert
    suspend fun insert(pregunta: PreguntaEntity)

    @Query("SELECT * FROM Pregunta WHERE id_pregunta = :id")
    suspend fun getById(id: Int): PreguntaEntity?

    @Query("SELECT COUNT(*) FROM Pregunta")
    suspend fun count(): Int
}