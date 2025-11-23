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
fun AdminJugadoresScreen(navController: NavHostController) {
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
        Text(text = "Administrar jugadores (eliminar / modificar puntajes)")

        // TODO: aquí pondremos la lista de usuarios y opciones para:
        // - eliminar jugador
        // - editar puntaje/puntaje global

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver
        androidx.compose.material3.Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AdminJugadoresScreenPreview(){
    val navController = rememberNavController()
    AdminJugadoresScreen(navController)
}