package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.screen.Login
import com.example.quizapp.ui.screen.Categoria
import com.example.quizapp.ui.screen.MenuInicioSesion
import com.example.quizapp.ui.screen.MenuOpciones
import com.example.quizapp.ui.screen.Perfil
import com.example.quizapp.ui.screen.Registro
import com.example.quizapp.ui.screen.Selecion
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // NavHost centralizado con todas tus pantallas
            NavHost(
                navController = navController,
                startDestination = Route.MenuInicioSesion.path
            ) {
                composable(Route.MenuInicioSesion.path) { MenuInicioSesion(navController) }
                composable(Route.Login.path) { Login(navController) }
                composable(Route.Registro.path) { Registro(navController) }
                composable(Route.MenuOpciones.path) { MenuOpciones(navController) }
                composable(Route.Perfil.path) { Perfil(navController) }
                composable(Route.Categoria.path) { Categoria(navController) }
                composable(Route.Selecion.path) { Selecion(navController) }
            }
        }
    }
}
