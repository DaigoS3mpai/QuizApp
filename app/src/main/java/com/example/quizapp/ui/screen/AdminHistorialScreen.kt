package com.example.quizapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar

@Composable
fun AdminHistorialScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar()
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Historial de partidas de jugadores")

        // TODO: aquí listaremos jugadores y al seleccionar, sus partidas (PartidaEntity)
        // con fecha, puntaje, categoría, dificultad, etc.

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun AdminHistorialScreenPreview(){
    val navController = rememberNavController()
    AdminHistorialScreen(navController)
}