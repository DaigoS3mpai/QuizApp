package com.example.quizapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.screen.auth.*
import com.example.quizapp.ui.screen.quiz.*
import com.example.quizapp.ui.screen.admin.*
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import com.example.quizapp.utils.sessionDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Composable
fun RootNavigation() {

    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    var startDestination by remember { mutableStateOf<String?>(null) }

    // 🔹 Al abrir la app, miramos DIRECTO en DataStore si hay user_id guardado
    LaunchedEffect(Unit) {
        val USER_ID_KEY = intPreferencesKey("user_id")

        val userId = context.sessionDataStore.data
            .map { prefs -> prefs[USER_ID_KEY] ?: -1 }
            .first()

        if (userId != -1) {
            // ✅ Hay sesión guardada → empezamos en el menú
            startDestination = Route.MenuOpciones.path
            // y en paralelo pedimos los datos completos al microservicio
            authViewModel.loadSession()
        } else {
            // ❌ No hay sesión → vamos a login / menú de inicio
            startDestination = Route.Login.path
        }
    }

    if (startDestination == null) {
        // Pantalla de carga mientras revisamos DataStore
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {

        // ------------------ AUTH ------------------
        composable(Route.Login.path) { Login(navController) }
        composable(Route.Registro.path) { Registro(navController) }
        composable(Route.Perfil.path) { Perfil(navController) }
        composable(Route.Password.path) { Password(navController) }
        composable(Route.EditProfile.path) { EditProfileScreen(navController) }

        // ------------------ MENÚS ------------------
        composable(Route.MenuInicioSesion.path) { MenuInicioSesion(navController) }
        composable(Route.MenuOpciones.path) { MenuOpciones(navController) }

        // ------------------ QUIZ + CATEGORÍAS ------------------

        // Categoría: recibe dificultadId
        composable(
            route = Route.CategoriaScreen.path,
            arguments = listOf(navArgument("dificultadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val dificultadId = backStackEntry.arguments?.getLong("dificultadId") ?: 1L
            CategoriaScreen(navController, dificultadId)
        }

        // Quiz: recibe dificultadId + categoriaId
        composable(
            route = Route.QuizScreen.path,
            arguments = listOf(
                navArgument("dificultadId") { type = NavType.LongType },
                navArgument("categoriaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dificultadId = backStackEntry.arguments?.getLong("dificultadId") ?: 1L
            val categoriaId = backStackEntry.arguments?.getLong("categoriaId") ?: 1L
            QuizScreen(navController, dificultadId, categoriaId)
        }

        // Selección de dificultad
        composable(Route.Selecion.path) { Selecion(navController) }

        // ------------------ ADMIN ------------------
        composable(Route.AdminMenu.path) { AdminMenuScreen(navController) }
        composable(Route.AdminJugadores.path) { AdminJugadoresScreen(navController) }
        composable(Route.AdminHistorial.path) { AdminHistorialScreen(navController) }
        composable(Route.AdminPreguntas.path) { AdminPreguntasScreen(navController) }
        composable(Route.AdminFeedback.path) { AdminFeedbackScreen(navController) }
        composable(Route.QuizMenu.path) { QuizMenuScreen(navController) }

        // Feedback jugador
        composable(Route.FeedbackJugador.path) { FeedbackScreen(navController) }
    }
}
