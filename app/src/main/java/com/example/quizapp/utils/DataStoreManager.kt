    package com.example.quizapp.utils

    import android.content.Context
    import androidx.datastore.preferences.preferencesDataStore

    // ✅ Solo UNA definición global de DataStore
    val Context.sessionDataStore by preferencesDataStore("session_prefs")
