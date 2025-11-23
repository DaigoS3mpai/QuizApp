package com.example.quizapp.data.feedback

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FeedbackDao {

    @Insert
    suspend fun insert(feedback: FeedbackEntity): Long

    // Feedback de un usuario (para que vea lo que ha enviado)
    @Query(
        """
        SELECT * FROM Feedback 
        WHERE Usuario_id_usuario = :userId 
        ORDER BY id_feedback DESC
        """
    )
    suspend fun getByUsuario(userId: Int): List<FeedbackEntity>

    // Todos los feedback, con nombre del jugador (para Admin / Quiz)
    @Query(
        """
        SELECT 
            F.id_feedback, F.mensaje, F.tipo, F.destino, F.fecha, F.resuelto, 
            F.Usuario_id_usuario,
            U.nombre AS nombre_usuario
        FROM Feedback F
        JOIN Usuario U ON U.id_usuario = F.Usuario_id_usuario
        WHERE (:soloPendientes = 0 OR F.resuelto = 0)
        ORDER BY F.id_feedback DESC
        """
    )
    suspend fun getTodosConUsuario(soloPendientes: Int = 0): List<FeedbackConUsuario>

    @Query("UPDATE Feedback SET resuelto = 1 WHERE id_feedback = :id")
    suspend fun marcarResuelto(id: Int)
}
