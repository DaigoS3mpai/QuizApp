package com.example.quizapp.data.opciones

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OpcionesDao {
    @Insert
    suspend fun insert(opcion: OpcionesEntity)

    @Query("SELECT * FROM Opciones WHERE Pregunta_id_pregunta = :preguntaId")
    suspend fun getOpcionesPorPregunta(preguntaId: Int): List<OpcionesEntity>

    @Query("SELECT COUNT(*) FROM Opciones")
    suspend fun count(): Int

    // 🔹 Insertar varias opciones de una vez
    @Insert
    suspend fun insertAll(opciones: List<OpcionesEntity>)

    // 🔹 Eliminar todas las opciones de una pregunta
    @Query("DELETE FROM Opciones WHERE pregunta_id_pregunta = :preguntaId")
    suspend fun deleteByPreguntaId(preguntaId: Int)

}
