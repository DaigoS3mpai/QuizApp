package com.example.quizapp.navegation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.screen.auth.*
import com.example.quizapp.ui.screen.quiz.*
import com.example.quizapp.ui.screen.admin.*
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import com.example.quizapp.utils.sessionDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Composable
fun RootNavigation(windowSizeClass: WindowSizeClass) {

    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    var startDestination by remember { mutableStateOf<String?>(null) }

    // 🔹 Al abrir la app, revisamos DataStore por user_id
    LaunchedEffect(Unit) {
        val USER_ID_KEY = intPreferencesKey("user_id")

        val userId = context.sessionDataStore.data
            .map { prefs -> prefs[USER_ID_KEY] ?: -1 }
            .first()

        startDestination =
            if (userId != -1) {
                authViewModel.loadSession()
                Route.MenuOpciones.path
            } else Route.Login.path
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 🔹 Saber en qué pantalla estamos
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 🔹 Rutas donde NO mostramos botón atrás
    val noBackRoutes = listOf(
        Route.Login.path,
        Route.MenuInicioSesion.path,
        Route.MenuOpciones.path,
        Route.Perfil.path
    )

    val showBackButton =
        navController.previousBackStackEntry != null && currentRoute !in noBackRoutes

    //Asignar título dinámico por pantalla
    val dynamicTitle = when {
        currentRoute == Route.Login.path -> "Iniciar Sesión"
        currentRoute == Route.Registro.path -> "Crear Cuenta"
        currentRoute == Route.Perfil.path -> "Mi Perfil"
        currentRoute?.startsWith("password") == true -> "Cambiar Contraseña"
        currentRoute == Route.EditProfile.path -> "Editar Perfil"
        currentRoute == Route.MenuInicioSesion.path -> "Bienvenido"
        currentRoute == Route.MenuOpciones.path -> "Menú Principal"
        currentRoute == Route.Selecion.path -> "Selecciona Dificultad"
        currentRoute == Route.AdminMenu.path -> "Panel Administrador"
        currentRoute == Route.AdminJugadores.path -> "Jugadores"
        currentRoute == Route.AdminHistorial.path -> "Historial"
        currentRoute == Route.AdminPreguntas.path -> "Preguntas"
        currentRoute == Route.AdminFeedback.path -> "Feedback"
        currentRoute == Route.QuizMenu.path -> "Menú Quiz"
        currentRoute == Route.FeedbackJugador.path -> "Mi Feedback"
        currentRoute == Route.CategoriaScreen.path -> "Categorías"
        currentRoute == Route.QuizScreen.path -> "Quiz"
        else -> "Retro Flash"
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                showBackButton = showBackButton,
                onBackClick = { navController.popBackStack() },
                windowSizeClass = windowSizeClass
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ------------------ AUTH ------------------
            composable(Route.Login.path) { Login(navController) }
            composable(Route.Registro.path) { Registro(navController) }
            composable(Route.Perfil.path) { Perfil(navController) }
            composable(
                route = Route.Password.path,
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType },
                    navArgument("token") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                val token = backStackEntry.arguments?.getString("token") ?: ""
                Password(navController, email, token)
            }
            composable(Route.EditProfile.path) { EditProfileScreen(navController) }
            composable(Route.MisPartidas.path) { MisPartidasScreen(navController) }
            composable(Route.ForgotPasswordEmail.path) {
                ForgotPasswordEmail(navController)
            }
            composable(
                route = Route.VerifySecurityQuestions.path,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                SecurityQuestionsVerify(navController, email)
            }

            composable(
                route = Route.SetupSecurityQuestions.path,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                SetupSecurityQuestionsScreen(navController, userId)
            }



            // ------------------ MENÚS ------------------
            composable(Route.MenuInicioSesion.path) { MenuInicioSesion(navController) }
            composable(Route.MenuOpciones.path) { MenuOpciones(navController) }

            // ------------------ QUIZ + CATEGORÍAS ------------------
            composable(
                route = Route.CategoriaScreen.path,
                arguments = listOf(navArgument("dificultadId") { type = NavType.LongType })
            ) { backStackEntry ->
                val dificultadId = backStackEntry.arguments?.getLong("dificultadId") ?: 1L
                CategoriaScreen(navController, dificultadId)
            }

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

            composable(Route.Selecion.path) { Selecion(navController) }

            // ------------------ ADMIN ------------------
            composable(Route.AdminMenu.path) { AdminMenuScreen(navController) }
            composable(Route.AdminJugadores.path) { AdminJugadoresScreen(navController) }
            composable(Route.AdminHistorial.path) { AdminHistorialScreen(navController) }
            composable(Route.AdminPreguntas.path) { AdminPreguntasScreen(navController) }
            composable(Route.AdminFeedback.path) { AdminFeedbackScreen(navController) }
            composable(Route.QuizMenu.path) { QuizMenuScreen(navController) }

            composable(Route.FeedbackJugador.path) { FeedbackScreen(navController) }
        }
    }
}
