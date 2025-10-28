package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.screen.*

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
            NavHost(
                navController = navController,
                startDestination = Route.MenuInicioSesion.path
            ) {
                // 🔹 Pantallas base
                composable(Route.MenuInicioSesion.path) { MenuInicioSesion(navController) }
                composable(Route.Login.path) { Login(navController) }
                composable(Route.Registro.path) { Registro(navController) }
                composable(Route.MenuOpciones.path) { MenuOpciones(navController) }
                composable(Route.Perfil.path) { Perfil(navController) }
                composable(Route.Selecion.path) { Selecion(navController) }
                composable(Route.Password.path) { Password(navController) }

                // 🔹 Pantalla única de categorías (recibe dificultadId dinámicamente)
                composable(
                    route = "Categoria/{dificultadId}",
                    arguments = listOf(
                        navArgument("dificultadId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val dificultadId = backStackEntry.arguments?.getInt("dificultadId") ?: 1
                    CategoriaScreen(navController, dificultadId)
                }

                // 🔹 Pantalla única del quiz (recibe dificultadId y categoriaId)
                composable(
                    route = "QuizFacil/{dificultadId}/{categoriaId}",
                    arguments = listOf(
                        navArgument("dificultadId") { type = NavType.IntType },
                        navArgument("categoriaId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val dificultadId = backStackEntry.arguments?.getInt("dificultadId") ?: 1
                    val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: 1
                    QuizScreen(navController, dificultadId, categoriaId)
                }
            }
        }
    }
}
