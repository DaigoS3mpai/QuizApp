package com.example.quizapp.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quizapp.data.*
import com.example.quizapp.data.categoria.CategoriaDao
import com.example.quizapp.data.categoria.CategoriaEntity
import com.example.quizapp.data.dificultad.DificultadDao
import com.example.quizapp.data.dificultad.DificultadEntity
import com.example.quizapp.data.estado.EstadoDao
import com.example.quizapp.data.estado.EstadoEntity
import com.example.quizapp.data.opciones.OpcionesDao
import com.example.quizapp.data.opciones.OpcionesEntity
import com.example.quizapp.data.partida.PartidaDao
import com.example.quizapp.data.partida.PartidaEntity
import com.example.quizapp.data.pregunta.PreguntaDao
import com.example.quizapp.data.pregunta.PreguntaEntity
import com.example.quizapp.data.rol.RolDao
import com.example.quizapp.data.rol.RolEntity
import com.example.quizapp.data.user.UserDao
import com.example.quizapp.data.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoriaEntity::class,
        DificultadEntity::class,
        EstadoEntity::class,
        RolEntity::class,
        UserEntity::class,
        PreguntaEntity::class,
        OpcionesEntity::class,
        PartidaEntity::class
    ],
    version = 13, // 🔹 Nueva versión para forzar recreación limpia
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // 🔹 Declaración de DAOs
    abstract fun categoriaDao(): CategoriaDao
    abstract fun dificultadDao(): DificultadDao
    abstract fun estadoDao(): EstadoDao
    abstract fun rolDao(): RolDao
    abstract fun usuarioDao(): UserDao
    abstract fun preguntaDao(): PreguntaDao
    abstract fun opcionesDao(): OpcionesDao
    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "db_appSQLITE.db"
        private const val TAG = "AppDatabaseInit"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d(TAG, "🟢 Creando base y agregando datos base...")

                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val rolDao = database.rolDao()
                                val estadoDao = database.estadoDao()
                                val categoriaDao = database.categoriaDao()
                                val dificultadDao = database.dificultadDao()

                                try {
                                    // Esperar para asegurar que Room haya creado las tablas
                                    delay(800)

                                    // --- ROL ---
                                    rolDao.insert(RolEntity(id_rol = 1, nombre = "Usuario"))
                                    rolDao.insert(RolEntity(id_rol = 2, nombre = "Administrador"))
                                    Log.d(TAG, "✅ Roles base insertados (Usuario / Administrador)")

                                    // --- ESTADO ---
                                    estadoDao.insert(EstadoEntity(id_estado = 1, nombre = "Activo"))
                                    estadoDao.insert(EstadoEntity(id_estado = 2, nombre = "Inactivo"))
                                    Log.d(TAG, "✅ Estados base insertados (Activo / Inactivo)")

                                    // --- CATEGORÍA ---
                                    categoriaDao.insert(CategoriaEntity(id_categoria = 1, nombre_categoria = "Arte"))
                                    categoriaDao.insert(CategoriaEntity(id_categoria = 2, nombre_categoria = "Deporte"))
                                    categoriaDao.insert(CategoriaEntity(id_categoria = 3, nombre_categoria = "Historia"))
                                    categoriaDao.insert(CategoriaEntity(id_categoria = 4, nombre_categoria = "Cine"))
                                    Log.d(TAG, "✅ Categorías base insertadas")

                                    // --- DIFICULTAD ---
                                    dificultadDao.insert(
                                        DificultadEntity(
                                            id_dificultad = 1,
                                            nombre_dificultad = "Fácil",
                                            tiempo_seg = "30",   // 🔸 tipo String según tu entidad
                                            multip_punt = 1
                                        )
                                    )
                                    dificultadDao.insert(
                                        DificultadEntity(
                                            id_dificultad = 2,
                                            nombre_dificultad = "Medio",
                                            tiempo_seg = "20",
                                            multip_punt = 2
                                        )
                                    )
                                    dificultadDao.insert(
                                        DificultadEntity(
                                            id_dificultad = 3,
                                            nombre_dificultad = "Difícil",
                                            tiempo_seg = "10",
                                            multip_punt = 3
                                        )
                                    )
                                    Log.d(TAG, "✅ Dificultades base insertadas")

                                    Log.d(TAG, "🎉 Datos base creados exitosamente")

                                    // --- LLAMAR AL SEEDER ---
                                    Log.d(TAG, "🚀 Ejecutando DatabaseSeeder para preguntas y opciones...")
                                    DatabaseSeeder.seed(context, database)

                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ Error al crear datos base: ${e.message}")
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // 🔹 Recrar si cambia el esquema
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
