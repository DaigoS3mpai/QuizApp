package com.example.quizapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.FeedbackViewModel
import com.example.quizapp.ui.viewmodel.FeedbackViewModelFactory

@Composable
fun AdminFeedbackScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: FeedbackViewModel =
        viewModel(factory = FeedbackViewModelFactory(context))

    val feedbackList by viewModel.todosFeedback.collectAsState()
    val error by viewModel.mensajeError.collectAsState()

    var soloPendientes by remember { mutableStateOf(true) }

    LaunchedEffect(soloPendientes) {
        viewModel.cargarTodosFeedback(soloPendientes)
    }

    Scaffold(
        topBar = {
            AppTopBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (soloPendientes) "Mostrando solo pendientes" else "Mostrando todos",
                    style = MaterialTheme.typography.titleMedium
                )

                FilterChip(
                    selected = soloPendientes,
                    onClick = { soloPendientes = !soloPendientes },
                    label = { Text(if (soloPendientes) "Pendientes" else "Todos") }
                )
            }

            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(feedbackList) { fb ->
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "De: ${fb.nombre_usuario} (id ${fb.usuario_id_usuario})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text("Fecha: ${fb.fecha}", style = MaterialTheme.typography.bodySmall)
                            Text("Tipo: ${fb.tipo} | Destino: ${fb.destino}")
                            Text(fb.mensaje)

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                if (fb.resuelto == 0) {
                                    TextButton(
                                        onClick = {
                                            viewModel.marcarResuelto(
                                                idFeedback = fb.id_feedback,
                                                soloPendientes = soloPendientes
                                            )
                                        }
                                    ) {
                                        Text("Marcar como resuelto")
                                    }
                                } else {
                                    Text(
                                        "✔ Resuelto",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Volver")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminFeedbackScreenPreview(){
    val navController = rememberNavController()
    AdminFeedbackScreen(navController)
}