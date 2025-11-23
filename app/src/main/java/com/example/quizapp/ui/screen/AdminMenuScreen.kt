package com.example.quizapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar

@Composable
fun AdminMenuScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar()
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel de Administrador",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = { navController.navigate(Route.AdminHistorial.path) }) {
            Text("Revisar historial de jugadores")
        }

        Button(onClick = { navController.navigate(Route.AdminJugadores.path) }) {
            Text("Gestionar jugadores (eliminar / puntajes)")
        }

        Button(onClick = { navController.navigate(Route.AdminPreguntas.path) }) {
            Text("CRUD de preguntas del quiz")
        }

        Button(onClick = { navController.navigate(Route.AdminFeedback.path) }) {
            Text("Feedback sobre errores en preguntas")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
    }
}
@Preview(showBackground = true)
@Composable
fun AdminMenuScreenPreview(){
    val navController = rememberNavController()
    AdminMenuScreen(navController)
}