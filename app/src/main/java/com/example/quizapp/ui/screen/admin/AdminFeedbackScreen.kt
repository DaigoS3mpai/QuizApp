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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.admin.AdminFeedbackViewModel
import com.example.quizapp.ui.viewmodel.admin.AdminFeedbackViewModelFactory
import com.example.quizapp.ui.viewmodel.admin.FeedbackFiltro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFeedbackScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: AdminFeedbackViewModel =
        viewModel(factory = AdminFeedbackViewModelFactory(context))

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

            Text(
                text = "Feedback de usuarios",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            // Filtros: Pendientes / Todos
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = state.filtro == FeedbackFiltro.PENDIENTES,
                    onClick = { viewModel.cambiarFiltro(FeedbackFiltro.PENDIENTES) },
                    label = { Text("Pendientes") }
                )
                FilterChip(
                    selected = state.filtro == FeedbackFiltro.TODOS,
                    onClick = { viewModel.cambiarFiltro(FeedbackFiltro.TODOS) },
                    label = { Text("Todos") }
                )
            }

            Spacer(Modifier.height(8.dp))

            state.errorMsg?.let {
                Text(text = it, color = Color.Red)
                Spacer(Modifier.height(4.dp))
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { fb ->
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
                                "ID #${fb.id}",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Usuario ID: ${fb.usuarioId}")
                            Text("Tipo: ${fb.tipo}")
                            Text("Destino: ${fb.destino}")
                            Text("Fecha: ${fb.fecha}")
                            Text(
                                "Estado: " + if (fb.resuelto) "Resuelto" else "Pendiente",
                                fontWeight = FontWeight.SemiBold,
                                color = if (fb.resuelto) Color(0xFF4CAF50) else Color(0xFFF57C00)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Mensaje:")
                            Text(fb.mensaje)

                            if (!fb.resuelto) {
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.resolverFeedback(fb.id) }
                                ) {
                                    Text("Marcar como resuelto")
                                }
                            }
                        }
                    }
                }
            }

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
fun AdminFeedbackScreenPreview() {
    val navController = rememberNavController()
    AdminFeedbackScreen(navController)
}
