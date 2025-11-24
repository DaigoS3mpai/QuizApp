package com.example.quizapp.ui.screen.admin

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.admin.AdminHistorialViewModel
import com.example.quizapp.ui.viewmodel.admin.AdminHistorialViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHistorialScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: AdminHistorialViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = AdminHistorialViewModelFactory(context)
        )

    val state by viewModel.uiState.collectAsState()

    val fondoAzul = Color(0xFF87CEEB)

    Scaffold(
        topBar = { AppTopBar() },
        containerColor = fondoAzul
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondoAzul)
                .padding(16.dp)
        ) {

            // 🔹 Filtro por ID de usuario
            OutlinedTextField(
                value = state.userIdText,
                onValueChange = viewModel::onUserIdChange,
                label = { Text("ID de usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.buscarHistorial() },
                    enabled = !state.isLoading
                ) {
                    Text("Buscar historial")
                }

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 Mensaje de error o información
            state.errorMsg?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 Lista de partidas
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.partidas) { partida ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                "Partida #${partida.id}",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Usuario ID: ${partida.usuarioId}")
                            Text("Categoría: ${partida.categoria}")
                            Text("Dificultad: ${partida.dificultad}")
                            Text("Fecha inicio: ${partida.fechaInicio}")
                            Text("Fecha fin: ${partida.fechaFin ?: "-"}")
                            Text("Puntaje final: ${partida.puntajeFinal}")
                            Text("Estado: ${partida.estado}")
                        }
                    }
                }
            }

            // 🔙 Botón volver
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHistorialScreenPreview() {
    val navController = rememberNavController()
    AdminHistorialScreen(navController)
}
