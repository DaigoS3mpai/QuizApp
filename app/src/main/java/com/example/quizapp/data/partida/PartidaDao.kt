package com.example.quizapp.data.partida

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PartidaDao {
    @Insert
    suspend fun insert(partida: PartidaEntity)

    @Query("SELECT * FROM Partida WHERE id_partida = :id")
    suspend fun getById(id: Int): PartidaEntity?

    @Query("SELECT COUNT(*) FROM Partida")
    suspend fun count(): Int
}