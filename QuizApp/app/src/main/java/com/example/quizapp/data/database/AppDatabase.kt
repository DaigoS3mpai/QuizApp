package com.example.quizapp.data.database

import android.content.Context                                  // Contexto para construir DB
import androidx.room.Database                                   // Anotación @Database
import androidx.room.Room                                       // Builder de DB
import androidx.room.RoomDatabase                               // Clase base de DB
import androidx.sqlite.db.SupportSQLiteDatabase                 // Tipo del callback onCreate
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
import kotlinx.coroutines.CoroutineScope                        // Para corrutinas en callback
import kotlinx.coroutines.Dispatchers                           // Dispatcher IO
import kotlinx.coroutines.launch                                // Lanzar corrutina

// @Database registra entidades y versión del esquema.
// version = 1: como es primera inclusión, partimos en 1.
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
    version = 1,
    exportSchema = true // Mantener true para inspección de esquema (útil en educación)
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos los DAOs
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
        private var INSTANCE: AppDatabase? = null              // Instancia singleton
        private const val DB_NAME = "db_appSQLITE.db"          // Nombre del archivo .db

        // Obtiene la instancia única de la base
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val instance = getInstance(context)

                                // Precarga de datos iniciales (ejemplo, ajusta según tus necesidades)
                                // Reemplaza con datos de prueba para tu BD
                                // Ejemplo para Categoria
                                instance.categoriaDao().insert(CategoriaEntity(id_categoria = 1, nombre_categoria = "Arte"))
                                // Añade más para otras tablas
                            }
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }
}