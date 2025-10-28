package com.example.quizapp.data.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.quizapp.R
import com.example.quizapp.data.opciones.OpcionesEntity
import com.example.quizapp.data.pregunta.PreguntaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object DatabaseSeeder {
    suspend fun seed(context: Context, db: AppDatabase) {
        withContext(Dispatchers.IO) {
            try {
                val preguntaDao = db.preguntaDao()
                val opcionesDao = db.opcionesDao()

                // 🔹 Cargar la imagen base (por ejemplo, el logo)
                val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)

                // 🔹 Reducir tamaño a 300x300 para aligerar el peso
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)

                // 🔹 Comprimir a formato JPEG calidad media
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val imagenBytes = stream.toByteArray()

                // 🔹 Insertar preguntas base
                val preguntas = listOf(
                    PreguntaEntity(
                        imagen = imagenBytes,
                        nombre = "¿Quién pintó la Mona Lisa?",
                        puntaje = 10,
                        estado_id_estado = 1,
                        categoria_id_categoria = 1,
                        dificultad_id_dificultad = 1
                    ),
                    PreguntaEntity(
                        imagen = imagenBytes,
                        nombre = "¿Cuántos jugadores hay en un equipo de fútbol?",
                        puntaje = 10,
                        estado_id_estado = 1,
                        categoria_id_categoria = 2,
                        dificultad_id_dificultad = 1
                    ),
                    PreguntaEntity(
                        imagen = imagenBytes,
                        nombre = "¿En qué año comenzó la Primera Guerra Mundial?",
                        puntaje = 10,
                        estado_id_estado = 1,
                        categoria_id_categoria = 3,
                        dificultad_id_dificultad = 1
                    ),
                    PreguntaEntity(
                        imagen = imagenBytes,
                        nombre = "¿Quién dirigió 'Titanic'?",
                        puntaje = 10,
                        estado_id_estado = 1,
                        categoria_id_categoria = 4,
                        dificultad_id_dificultad = 1
                    )
                )

                val preguntaIds = preguntas.map { preguntaDao.insert(it) }

                // 🔹 Insertar opciones base
                val opciones = listOf(
                    // Pregunta 1
                    OpcionesEntity(texto = "Leonardo da Vinci", correcta = 1, pregunta_id_pregunta = preguntaIds[0].toInt()),
                    OpcionesEntity(texto = "Miguel Ángel", correcta = 0, pregunta_id_pregunta = preguntaIds[0].toInt()),
                    OpcionesEntity(texto = "Rembrandt", correcta = 0, pregunta_id_pregunta = preguntaIds[0].toInt()),
                    OpcionesEntity(texto = "Picasso", correcta = 0, pregunta_id_pregunta = preguntaIds[0].toInt()),

                    // Pregunta 2
                    OpcionesEntity(texto = "11", correcta = 1, pregunta_id_pregunta = preguntaIds[1].toInt()),
                    OpcionesEntity(texto = "10", correcta = 0, pregunta_id_pregunta = preguntaIds[1].toInt()),
                    OpcionesEntity(texto = "12", correcta = 0, pregunta_id_pregunta = preguntaIds[1].toInt()),
                    OpcionesEntity(texto = "9", correcta = 0, pregunta_id_pregunta = preguntaIds[1].toInt()),

                    // Pregunta 3
                    OpcionesEntity(texto = "1914", correcta = 1, pregunta_id_pregunta = preguntaIds[2].toInt()),
                    OpcionesEntity(texto = "1918", correcta = 0, pregunta_id_pregunta = preguntaIds[2].toInt()),
                    OpcionesEntity(texto = "1939", correcta = 0, pregunta_id_pregunta = preguntaIds[2].toInt()),
                    OpcionesEntity(texto = "1920", correcta = 0, pregunta_id_pregunta = preguntaIds[2].toInt()),

                    // Pregunta 4
                    OpcionesEntity(texto = "James Cameron", correcta = 1, pregunta_id_pregunta = preguntaIds[3].toInt()),
                    OpcionesEntity(texto = "Steven Spielberg", correcta = 0, pregunta_id_pregunta = preguntaIds[3].toInt()),
                    OpcionesEntity(texto = "Christopher Nolan", correcta = 0, pregunta_id_pregunta = preguntaIds[3].toInt()),
                    OpcionesEntity(texto = "Ridley Scott", correcta = 0, pregunta_id_pregunta = preguntaIds[3].toInt())
                )

                opciones.forEach { opcionesDao.insert(it) }

                Log.d("DatabaseSeeder", "✅ Datos iniciales insertados correctamente con ${preguntaIds.size} preguntas y ${opciones.size} opciones")

            } catch (e: Exception) {
                Log.e("DatabaseSeeder", "❌ Error al insertar datos iniciales: ${e.message}")
            }
        }
    }
}
