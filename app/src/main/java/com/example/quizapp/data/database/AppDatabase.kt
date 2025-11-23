package com.example.quizapp.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.quizapp.data.feedback.FeedbackDao
import com.example.quizapp.data.feedback.FeedbackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        PartidaEntity::class,
        FeedbackEntity::class
    ],
    version = 20,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun dificultadDao(): DificultadDao
    abstract fun estadoDao(): EstadoDao
    abstract fun rolDao(): RolDao
    abstract fun usuarioDao(): UserDao
    abstract fun preguntaDao(): PreguntaDao
    abstract fun opcionesDao(): OpcionesDao
    abstract fun partidaDao(): PartidaDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "db_appSQLITE.db"
        private const val TAG = "DB_INIT"

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

                            Log.d(TAG, "🟡 [SEED] Base creada. Iniciando inserción de datos base...")

                            // AHORA: seeding ASÍNCRONO, sin bloquear la creación de la BD
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = INSTANCE
                                if (database == null) {
                                    Log.e(TAG, "❌ [SEED ERROR] INSTANCE es null durante onCreate()")
                                    return@launch
                                }

                                try {
                                    val rolDao = database.rolDao()
                                    val estadoDao = database.estadoDao()
                                    val categoriaDao = database.categoriaDao()
                                    val dificultadDao = database.dificultadDao()

                                    // ---- ROLES ----
                                    rolDao.insert(RolEntity(1, "Usuario"))
                                    rolDao.insert(RolEntity(2, "Administrador"))
                                    rolDao.insert(RolEntity(3, "Quiz"))
                                    Log.d(TAG, "🟢 [SEED] Roles insertados")

                                    // ---- ESTADOS ----
                                    estadoDao.insert(EstadoEntity(1, "Activo"))
                                    estadoDao.insert(EstadoEntity(2, "Inactivo"))
                                    Log.d(TAG, "🟢 [SEED] Estados insertados")

                                    // ---- CATEGORIAS ----
                                    categoriaDao.insert(CategoriaEntity(1, "Arte"))
                                    categoriaDao.insert(CategoriaEntity(2, "Deporte"))
                                    categoriaDao.insert(CategoriaEntity(3, "Historia"))
                                    categoriaDao.insert(CategoriaEntity(4, "Cine"))
                                    Log.d(TAG, "🟢 [SEED] Categorías insertadas")

                                    // ---- DIFICULTADES ----
                                    dificultadDao.insert(DificultadEntity(1, "Fácil", "30", 1))
                                    dificultadDao.insert(DificultadEntity(2, "Medio", "20", 2))
                                    dificultadDao.insert(DificultadEntity(3, "Difícil", "10", 3))
                                    Log.d(TAG, "🟢 [SEED] Dificultades insertadas")

                                    // ---- SEEDER (preguntas y opciones) ----
                                    Log.d(TAG, "🟡 [SEED] Ejecutando DatabaseSeeder...")
                                    DatabaseSeeder.seed(context, database)
                                    Log.d(TAG, "🟢 [SEED] Ejecución de DatabaseSeeder completa")

                                    Log.d(TAG, "🎉 [SEED] Base de datos completamente cargada y lista")

                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ [SEED ERROR] ${e.message}", e)
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d(TAG, "📂 Base de datos abierta correctamente.")
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
