package com.example.quizapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.data.feedback.FeedbackDao
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.AuthViewModel
import com.example.quizapp.ui.viewmodel.AuthViewModelFactory
import com.example.quizapp.ui.viewmodel.FeedbackViewModel
import com.example.quizapp.ui.viewmodel.FeedbackViewModelFactory
import android.widget.Toast


@Composable
fun FeedbackScreen(navController: NavHostController) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel =
        viewModel(factory = AuthViewModelFactory(context))

    // 🔹 Cargar sesión desde DataStore (igual que en MenuOpciones)
    LaunchedEffect(Unit) {
        authViewModel.loadSession()
    }

    val currentUser by authViewModel.currentUser.collectAsState()

    val feedbackViewModel: FeedbackViewModel =
        viewModel(factory = FeedbackViewModelFactory(context))

    val misFeedback by feedbackViewModel.misFeedback.collectAsState()
    val enviando by feedbackViewModel.enviando.collectAsState()
    val error by feedbackViewModel.mensajeError.collectAsState()

    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("PREGUNTA") } // PREGUNTA / PUNTAJE / OTRO
    var destino by remember { mutableStateOf("QUIZ") }  // QUIZ / ADMIN / AMBOS


    // Cuando tengamos usuario, cargamos su feedback
    LaunchedEffect(currentUser.id) {
        if (currentUser.id != 0) {
            feedbackViewModel.cargarMisFeedback(currentUser.id)
        }
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
            Text(
                text = "Cuéntanos si encontraste un error en una pregunta o un problema con tu puntaje.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Tipo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Tipo:")
                FilterChip(
                    selected = tipo == "PREGUNTA",
                    onClick = { tipo = "PREGUNTA" },
                    label = { Text("Pregunta") }
                )
                FilterChip(
                    selected = tipo == "PUNTAJE",
                    onClick = { tipo = "PUNTAJE" },
                    label = { Text("Puntaje") }
                )
                FilterChip(
                    selected = tipo == "OTRO",
                    onClick = { tipo = "OTRO" },
                    label = { Text("Otro") }
                )
            }

            // Destino
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Enviar a:")
                FilterChip(
                    selected = destino == "QUIZ",
                    onClick = { destino = "QUIZ" },
                    label = { Text("Usuario Quiz") }
                )
                FilterChip(
                    selected = destino == "ADMIN",
                    onClick = { destino = "ADMIN" },
                    label = { Text("Admin") }
                )
                FilterChip(
                    selected = destino == "AMBOS",
                    onClick = { destino = "AMBOS" },
                    label = { Text("Ambos") }
                )
            }

            Button(
                onClick = {
                    if (mensaje.isBlank() || enviando) return@Button

                    if (currentUser.id == 0) {
                        Toast.makeText(
                            context,
                            "Debes iniciar sesión para enviar feedback",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    feedbackViewModel.enviarFeedback(
                        userId = currentUser.id,
                        mensaje = mensaje,
                        tipo = tipo,
                        destino = destino
                    )
                    mensaje = ""

                    // recargar la lista del usuario después de enviar
                    feedbackViewModel.cargarMisFeedback(currentUser.id)
                },
                enabled = !enviando && mensaje.isNotBlank()
            ) {
                Text(if (enviando) "Enviando..." else "Enviar feedback")
            }



            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Mis feedback enviados:",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(misFeedback) { fb ->
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text("Fecha: ${fb.fecha}", style = MaterialTheme.typography.bodySmall)
                            Text("Tipo: ${fb.tipo}  |  Destino: ${fb.destino}")
                            Text(fb.mensaje)
                            if (fb.resuelto == 1) {
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
fun FeedbackScreenPreview(){
    val navController = rememberNavController()
    FeedbackScreen(navController)
}