package com.example.quizapp.ui.screen

import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.utils.obtenerWindowSizeClass

@Composable
fun CalcularTamPantalla(screenName: String) {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> callScreen(screenName)
        WindowWidthSizeClass.Medium -> callScreen(screenName)
        WindowWidthSizeClass.Expanded -> callScreen(screenName + "Expand")
    }
}

@Composable
fun callScreen(screenName: String) {
    when (screenName) {
        "Categoria" -> Categoria(navController = rememberNavController())
        "CategoriaExpand" -> CategoriaExpand(navController = rememberNavController())
        "Login" -> Login(navController = rememberNavController())
        "LoginExpand" -> LoginExpand(navController = rememberNavController())
        "MenuInicioSesion" -> MenuInicioSesion(navController = rememberNavController())
        "MenuInicioSesionExpand" -> MenuInicioSesionExpand(navController = rememberNavController())
        "MenuOpciones" -> MenuOpciones(navController = rememberNavController())
        "MenuOpcionesExpand" -> MenuOpcionesExpand(navController = rememberNavController())
        "Perfil" -> Perfil(navController = rememberNavController())
        "PerfilExpand" -> PerfilExpand(navController = rememberNavController())
        "Registro" -> Registro(navController = rememberNavController())
        "RegistroExpand" -> RegistroExpand(navController = rememberNavController())
        "Seleccion" -> Selecion(navController = rememberNavController())
        "SeleccionExpand" -> SeleccionExpand(navController = rememberNavController())
        else -> Text("Pantalla no encontrada: $screenName")
    }
}

