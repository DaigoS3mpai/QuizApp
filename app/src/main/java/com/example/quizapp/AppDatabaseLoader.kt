package com.example.quizapp

import android.app.Application
import android.util.Log
import com.example.quizapp.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseLoader : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("AppDatabaseLoader", "🚀 Iniciando carga temprana de la base de datos...")

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(applicationContext)

            // 👇 ESTO ES LO IMPORTANTE PARA FORZAR onCreate/onOpen
            db.openHelper.writableDatabase

            Log.d("AppDatabaseLoader", "🟢 Base de datos inicializada (openHelper).")
        }
    }
}
