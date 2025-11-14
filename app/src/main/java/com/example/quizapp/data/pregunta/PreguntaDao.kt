package com.example.quizapp.data.pregunta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings

@Dao
interface PreguntaDao {

    // 🔹 Inserta una pregunta y devuelve el ID generado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pregunta: PreguntaEntity): Long

    // 🔹 Inserta varias preguntas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preguntas: List<PreguntaEntity>)

    // 🔹 Obtiene una pregunta completa (con imagen)
    @Query("SELECT * FROM Pregunta WHERE id_pregunta = :id")
    suspend fun getById(id: Int): PreguntaEntity?

    // 🔹 Cuenta total de preguntas
    @Query("SELECT COUNT(*) FROM Pregunta")
    suspend fun count(): Int

    // ⚡ Versión liviana SIN cargar imagen (usa PreguntaLite)
    @Query("""
        SELECT id_pregunta, nombre, puntaje, 
               Estado_id_estado, Categoria_id_categoria, Dificultad_id_dificultad
        FROM Pregunta
        WHERE Dificultad_id_dificultad = :dificultadId 
        AND Categoria_id_categoria = :categoriaId
    """)
    @Suppress(RoomWarnings.CURSOR_MISMATCH)
    suspend fun getPreguntasLivianasPorDificultadYCategoria(
        dificultadId: Int,
        categoriaId: Int
    ): List<PreguntaLite>

    // 🔹 Obtiene solo la imagen
    @Query("SELECT imagen FROM Pregunta WHERE id_pregunta = :id")
    suspend fun getImagenPorId(id: Int): ByteArray?
}
