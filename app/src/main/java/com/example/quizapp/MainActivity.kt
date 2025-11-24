package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.quizapp.navigation.RootNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    RootNavigation()
                }

                // 🔹 Pantallas de administrador
                composable(Route.AdminMenu.path) {
                    AdminMenuScreen(navController)
                }
                composable(Route.AdminJugadores.path) {
                    AdminJugadoresScreen(navController)
                }
                composable(Route.AdminHistorial.path) {
                    AdminHistorialScreen(navController)
                }
                composable(Route.AdminPreguntas.path) {
                    AdminPreguntasScreen(navController)
                }
                composable(Route.AdminFeedback.path) {
                    AdminFeedbackScreen(navController)
                }
                composable(Route.FeedbackJugador.path) {
                    FeedbackScreen(navController)
                }
            }
        }
    }
}
