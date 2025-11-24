package com.example.quizapp.data.local

import android.content.Context

class PerfilPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("perfil_prefs", Context.MODE_PRIVATE)

    fun guardarFotoPerfil(uri: String) {
        prefs.edit().putString("foto_perfil_uri", uri).apply()
    }

    fun obtenerFotoPerfil(): String? =
        prefs.getString("foto_perfil_uri", null)
}
