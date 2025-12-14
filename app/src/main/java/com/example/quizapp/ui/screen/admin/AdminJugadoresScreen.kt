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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.admin.AdminJugadoresViewModel
import com.example.quizapp.ui.viewmodel.admin.AdminJugadoresViewModelFactory

@Composable
fun AdminJugadoresScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: AdminJugadoresViewModel =
        viewModel(factory = AdminJugadoresViewModelFactory(context))

    val usuarios by viewModel.usuarios.collectAsState()
    val error by viewModel.error.collectAsState()

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
        ) {

            // Mensaje de error (si hay)
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarios) { user ->

                    var puntaje by remember(user.id) { mutableStateOf(user.puntaje.toString()) }
                    var puntajeGlobal by remember(user.id) { mutableStateOf(user.puntajeGlobal.toString()) }

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
                            Text("Nombre: ${user.nombre}")
                            Text("Correo: ${user.correo}")
                            Text("Rol: ${user.rol ?: "Sin rol"}")
                            Spacer(Modifier.height(4.dp))

                            OutlinedTextField(
                                value = puntaje,
                                onValueChange = { puntaje = it },
                                label = { Text("Puntaje") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = puntajeGlobal,
                                onValueChange = { puntajeGlobal = it },
                                label = { Text("Puntaje Global") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.actualizarPuntaje(
                                            user.id,
                                            puntaje.toIntOrNull() ?: user.puntaje,
                                            puntajeGlobal.toIntOrNull() ?: user.puntajeGlobal
                                        )
                                    }
                                ) {
                                    Text("Guardar")
                                }

                                Button(
                                    onClick = {
                                        viewModel.eliminarJugador(user.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFDF3C3C),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Eliminar")
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
                    .padding(8.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminJugadoresScreenPreview() {
    val navController = rememberNavController()
    AdminJugadoresScreen(navController)
}
