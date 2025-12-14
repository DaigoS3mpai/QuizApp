package com.example.quizapp.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import com.example.quizapp.ui.viewmodel.Auth.PartidaUsuarioUi
import com.example.quizapp.ui.viewmodel.Auth.PartidasViewModel

@Composable
fun MisPartidasScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel =
        viewModel(factory = AuthViewModelFactory(LocalContext.current)),
    partidasViewModel: PartidasViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val uiState by partidasViewModel.uiState.collectAsState()

    // 1️⃣ Cargar la sesión para tener el usuario logeado en ESTE ViewModel
    LaunchedEffect(Unit) {
        authViewModel.loadSession()
    }

    // 2️⃣ Cuando ya tengamos usuario logeado con id > 0, cargamos sus partidas
    LaunchedEffect(currentUser.id, currentUser.loggedIn) {
        if (currentUser.loggedIn && currentUser.id > 0) {
            partidasViewModel.cargarPartidasUsuario(currentUser.id)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Historial de partidas",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                // ----------------- CONTENIDO -----------------
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = Color.Red
                        )
                    }

                    uiState.partidas.isEmpty() -> {
                        Text(
                            text = "Aún no tienes partidas registradas.",
                            fontSize = 18.sp
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.partidas) { partida ->
                                PartidaCard(partida = partida)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PartidaCard(partida: PartidaUsuarioUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFBEE8FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Fecha inicio: ${partida.fechaInicio}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            if (partida.fechaFin != null) {
                Text("Fecha fin: ${partida.fechaFin}", fontSize = 14.sp)
            }
            Text("Categoría: ${partida.categoria}", fontSize = 14.sp)
            Text("Dificultad: ${partida.dificultad}", fontSize = 14.sp)
            Text("Puntaje: ${partida.puntaje}", fontSize = 14.sp)
            Text("Estado: ${partida.estado}", fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisPartidasPreview() {
    val navController = rememberNavController()
    MisPartidasScreen(navController = navController)
}
