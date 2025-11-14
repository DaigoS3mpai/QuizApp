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

                // 🔹 Imagen base comprimida
                val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val imagenBytes = stream.toByteArray()

                // ====================================================
                // 🧩 PREGUNTAS
                // ====================================================
                val preguntas = listOf(
                    // ===== ARTE =====
                    PreguntaEntity(0, imagenBytes, "¿Quién pintó la Mona Lisa?", 10, 1, 1, 1),
                    PreguntaEntity(0, imagenBytes, "¿Qué escultor creó el David?", 10, 1, 1, 1),
                    PreguntaEntity(0, imagenBytes, "¿Dónde nació Picasso?", 10, 1, 1, 1),
                    PreguntaEntity(0, imagenBytes, "¿Qué pintor cortó su oreja?", 15, 1, 1, 2),
                    PreguntaEntity(0, imagenBytes, "¿Qué movimiento lideró Dalí?", 15, 1, 1, 2),
                    PreguntaEntity(0, imagenBytes, "¿Quién pintó 'La última cena'?", 20, 1, 1, 3),
                    PreguntaEntity(0, imagenBytes, "¿Quién pintó 'Los girasoles'?", 20, 1, 1, 3),

                    // ===== DEPORTE =====
                    PreguntaEntity(0, imagenBytes, "¿Cuántos jugadores hay en un equipo de fútbol?", 10, 1, 2, 1),
                    PreguntaEntity(0, imagenBytes, "¿En qué deporte se usa raqueta?", 10, 1, 2, 1),
                    PreguntaEntity(0, imagenBytes, "¿Cuántos anillos olímpicos existen?", 10, 1, 2, 1),
                    PreguntaEntity(0, imagenBytes, "¿Qué país ganó más Copas del Mundo?", 15, 1, 2, 2),
                    PreguntaEntity(0, imagenBytes, "¿Dónde se jugó el primer Mundial?", 15, 1, 2, 2),
                    PreguntaEntity(0, imagenBytes, "¿Quién ganó el Balón de Oro 2023?", 20, 1, 2, 3),
                    PreguntaEntity(0, imagenBytes, "¿Qué nadador tiene más medallas olímpicas?", 20, 1, 2, 3),

                    // ===== HISTORIA =====
                    PreguntaEntity(0, imagenBytes, "¿Cuándo comenzó la Primera Guerra Mundial?", 10, 1, 3, 1),
                    PreguntaEntity(0, imagenBytes, "¿Quién fue el primer presidente de EE.UU.?", 10, 1, 3, 1),
                    PreguntaEntity(0, imagenBytes, "¿En qué continente está Egipto?", 10, 1, 3, 1),
                    PreguntaEntity(0, imagenBytes, "¿Cuándo llegó el hombre a la Luna?", 15, 1, 3, 2),
                    PreguntaEntity(0, imagenBytes, "¿Qué dinastía comenzó la Gran Muralla China?", 15, 1, 3, 2),
                    PreguntaEntity(0, imagenBytes, "¿Qué tratado terminó la Primera Guerra Mundial?", 20, 1, 3, 3),
                    PreguntaEntity(0, imagenBytes, "¿Quién lideró la Revolución Rusa?", 20, 1, 3, 3),

                    // ===== CINE =====
                    PreguntaEntity(0, imagenBytes, "¿Quién dirigió 'Titanic'?", 10, 1, 4, 1),
                    PreguntaEntity(0, imagenBytes, "¿En qué película aparece Simba?", 10, 1, 4, 1),
                    PreguntaEntity(0, imagenBytes, "¿Qué actor interpretó a Iron Man?", 10, 1, 4, 1),
                    PreguntaEntity(0, imagenBytes, "¿En qué año se estrenó 'El Padrino'?", 15, 1, 4, 2),
                    PreguntaEntity(0, imagenBytes, "¿Quién dirigió 'Parásitos'?", 15, 1, 4, 2),
                    PreguntaEntity(0, imagenBytes, "¿Qué película tiene más premios Óscar?", 20, 1, 4, 3),
                    PreguntaEntity(0, imagenBytes, "¿Quién compuso la banda sonora de 'El Rey León'?", 20, 1, 4, 3)
                )

                val preguntaIds = preguntas.map { preguntaDao.insert(it).toInt() }

                // ====================================================
                // 🎯 OPCIONES
                // ====================================================
                val opciones = mutableListOf<OpcionesEntity>()

                fun addOpciones(id: Int, correct: String, vararg incorrectas: String) {
                    // Opción correcta
                    opciones.add(OpcionesEntity(id_opcion = 0, texto = correct, correcta = 1, pregunta_id_pregunta = id))

                    // Opciones incorrectas
                    incorrectas.forEach {
                        opciones.add(OpcionesEntity(id_opcion = 0, texto = it, correcta = 0, pregunta_id_pregunta = id))
                    }
                }

                // ===== ARTE =====
                addOpciones(preguntaIds[0], "Leonardo da Vinci", "Miguel Ángel", "Picasso", "Rembrandt")
                addOpciones(preguntaIds[1], "Miguel Ángel", "Donatello", "Bernini", "Rodin")
                addOpciones(preguntaIds[2], "España", "Italia", "Francia", "México")
                addOpciones(preguntaIds[3], "Van Gogh", "Monet", "Cézanne", "Renoir")
                addOpciones(preguntaIds[4], "Surrealismo", "Cubismo", "Fauvismo", "Impresionismo")
                addOpciones(preguntaIds[5], "Leonardo da Vinci", "Tiziano", "Rafael", "Caravaggio")
                addOpciones(preguntaIds[6], "Van Gogh", "Monet", "Gauguin", "Klimt")

                // ===== DEPORTE =====
                addOpciones(preguntaIds[7], "11", "10", "12", "9")
                addOpciones(preguntaIds[8], "Tenis", "Béisbol", "Rugby", "Golf")
                addOpciones(preguntaIds[9], "5", "6", "4", "7")
                addOpciones(preguntaIds[10], "Brasil", "Alemania", "Italia", "Argentina")
                addOpciones(preguntaIds[11], "Uruguay", "Brasil", "Inglaterra", "Italia")
                addOpciones(preguntaIds[12], "Lionel Messi", "Haaland", "Mbappé", "Modric")
                addOpciones(preguntaIds[13], "Michael Phelps", "Ian Thorpe", "Mark Spitz", "Ryan Lochte")

                // ===== HISTORIA =====
                addOpciones(preguntaIds[14], "1914", "1918", "1939", "1920")
                addOpciones(preguntaIds[15], "George Washington", "Lincoln", "Jefferson", "Adams")
                addOpciones(preguntaIds[16], "África", "Asia", "Europa", "Oceanía")
                addOpciones(preguntaIds[17], "1969", "1968", "1970", "1972")
                addOpciones(preguntaIds[18], "Qin", "Han", "Ming", "Tang")
                addOpciones(preguntaIds[19], "Tratado de Versalles", "Tratado de París", "Tratado de Viena", "Tratado de Utrecht")
                addOpciones(preguntaIds[20], "Lenin", "Stalin", "Trotsky", "Kerenski")

                // ===== CINE =====
                addOpciones(preguntaIds[21], "James Cameron", "Spielberg", "Peter Jackson", "Ridley Scott")
                addOpciones(preguntaIds[22], "El Rey León", "Bambi", "Tarzán", "Aladdín")
                addOpciones(preguntaIds[23], "Robert Downey Jr.", "Chris Evans", "Chris Hemsworth", "Mark Ruffalo")
                addOpciones(preguntaIds[24], "1972", "1970", "1974", "1976")
                addOpciones(preguntaIds[25], "Bong Joon-ho", "Park Chan-wook", "Lee Chang-dong", "Kim Ki-duk")
                addOpciones(preguntaIds[26], "Titanic", "El Retorno del Rey", "Ben-Hur", "Avatar")
                addOpciones(preguntaIds[27], "Hans Zimmer", "John Williams", "James Horner", "Alan Menken")

                // Insertar opciones
                opciones.forEach { opcionesDao.insert(it) }

                Log.d("DatabaseSeeder", "✅ Se insertaron ${preguntaIds.size} preguntas y ${opciones.size} opciones correctamente.")
            } catch (e: Exception) {
                Log.e("DatabaseSeeder", "❌ Error al insertar datos: ${e.message}", e)
            }
        }
    }
}
