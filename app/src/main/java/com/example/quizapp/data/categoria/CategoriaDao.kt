package com.example.quizapp.data.categoria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    @Query("SELECT * FROM Categoria WHERE id_categoria = :id")
    suspend fun getById(id: Int): CategoriaEntity?

    @Query("SELECT COUNT(*) FROM Categoria")
    suspend fun count(): Int

    @Query("SELECT * FROM Categoria")
    suspend fun getAll(): List<CategoriaEntity>
}
