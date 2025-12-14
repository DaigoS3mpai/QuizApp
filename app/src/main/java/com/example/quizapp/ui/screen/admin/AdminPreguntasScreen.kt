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
import com.example.quizapp.data.remote.dto.PreguntaAdminResponseDto
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.admin.AdminPreguntasViewModel
import com.example.quizapp.ui.viewmodel.admin.AdminPreguntasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPreguntasScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: AdminPreguntasViewModel =
        viewModel(factory = AdminPreguntasViewModelFactory(context))

    val state by viewModel.uiState.collectAsState()

    val fondoAzul = Color(0xFF87CEEB)

    // Estado del diálogo de creación
    var mostrarDialogo by remember { mutableStateOf(false) }
    var enunciado by remember { mutableStateOf("") }
    var categoriaIdTexto by remember { mutableStateOf("1") }
    var dificultadIdTexto by remember { mutableStateOf("1") }
    var estadoIdTexto by remember { mutableStateOf("1") }
    var opcionesTextos by remember { mutableStateOf(List(4) { "" }) }
    var indiceCorrecta by remember { mutableStateOf(0) }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var preguntaEnEdicion by remember { mutableStateOf<PreguntaAdminResponseDto?>(null) }


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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Preguntas (${state.preguntas.size})",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = { mostrarDialogo = true }) {
                    Text("Nueva pregunta")
                }
            }

            Spacer(Modifier.height(8.dp))

            state.errorMsg?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
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
                items(state.preguntas) { p ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                "#${p.id} - ${p.enunciado}",
                                fontWeight = FontWeight.Bold
                            )
                            Text("Categoría: ${p.categoria ?: "-"}")
                            Text("Dificultad: ${p.dificultad ?: "-"}")
                            Text("Estado: ${p.estado ?: "-"}")
                            Spacer(Modifier.height(4.dp))
                            Text("Opciones:")
                            p.opciones.forEach { o ->
                                Text(
                                    "• ${o.texto}" + if (o.esCorrecta) " (correcta)" else "",
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // preparar datos para edición
                                        preguntaEnEdicion = p
                                        enunciado = p.enunciado

                                        // rellenar máximo 4 opciones con lo que viene del backend
                                        val opciones = p.opciones
                                        opcionesTextos = List(4) { index ->
                                            opciones.getOrNull(index)?.texto ?: ""
                                        }

                                        // marcar la correcta
                                        indiceCorrecta = p.opciones.indexOfFirst { it.esCorrecta }
                                            .takeIf { it >= 0 } ?: 0

                                        mostrarDialogoEditar = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF2196F3) // azul
                                    )
                                ) {
                                    Text("Modificar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { viewModel.eliminarPregunta(p.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.Red
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
                    .padding(top = 8.dp)
            ) {
                Text("Volver")
            }
        }

        // 🔹 Diálogo para crear pregunta
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nueva pregunta") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = enunciado,
                            onValueChange = { enunciado = it },
                            label = { Text("Enunciado") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = categoriaIdTexto,
                            onValueChange = { categoriaIdTexto = it },
                            label = { Text("ID Categoría (1=Arte, 2=Deporte, etc.)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dificultadIdTexto,
                            onValueChange = { dificultadIdTexto = it },
                            label = { Text("ID Dificultad (1=Fácil, 2=Media, 3=Difícil)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = estadoIdTexto,
                            onValueChange = { estadoIdTexto = it },
                            label = { Text("ID Estado (ej. 1)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Opciones (marca cuál es correcta):")

                        opcionesTextos.forEachIndexed { index, texto ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = indiceCorrecta == index,
                                    onClick = { indiceCorrecta = index }
                                )
                                OutlinedTextField(
                                    value = texto,
                                    onValueChange = { value ->
                                        opcionesTextos =
                                            opcionesTextos.toMutableList().also { it[index] = value }
                                    },
                                    label = { Text("Opción ${index + 1}") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val categoriaId = categoriaIdTexto.toLongOrNull() ?: 1L
                            val dificultadId = dificultadIdTexto.toLongOrNull() ?: 1L
                            val estadoId = estadoIdTexto.toLongOrNull() ?: 1L

                            viewModel.crearPregunta(
                                enunciado = enunciado,
                                idCategoria = categoriaId,
                                idDificultad = dificultadId,
                                idEstado = estadoId,
                                textosOpciones = opcionesTextos,
                                indiceCorrecta = indiceCorrecta
                            )

                            // limpiar y cerrar
                            enunciado = ""
                            categoriaIdTexto = "1"
                            dificultadIdTexto = "1"
                            estadoIdTexto = "1"
                            opcionesTextos = List(4) { "" }
                            indiceCorrecta = 0
                            mostrarDialogo = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        if (mostrarDialogoEditar && preguntaEnEdicion != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEditar = false },
                title = { Text("Editar pregunta") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = enunciado,
                            onValueChange = { enunciado = it },
                            label = { Text("Enunciado") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = categoriaIdTexto,
                            onValueChange = { categoriaIdTexto = it },
                            label = { Text("ID Categoría (1=Arte, 2=Deporte, etc.)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dificultadIdTexto,
                            onValueChange = { dificultadIdTexto = it },
                            label = { Text("ID Dificultad (1=Fácil, 2=Media, 3=Difícil)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = estadoIdTexto,
                            onValueChange = { estadoIdTexto = it },
                            label = { Text("ID Estado (ej. 1)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Opciones (marca cuál es correcta):")

                        opcionesTextos.forEachIndexed { index, valor ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = indiceCorrecta == index,
                                    onClick = { indiceCorrecta = index }
                                )
                                OutlinedTextField(
                                    value = valor,
                                    onValueChange = { nuevo ->
                                        opcionesTextos = opcionesTextos.toMutableList().also {
                                            it[index] = nuevo
                                        }
                                    },
                                    label = { Text("Opción ${index + 1}") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val categoriaId = categoriaIdTexto.toLongOrNull() ?: 1L
                            val dificultadId = dificultadIdTexto.toLongOrNull() ?: 1L
                            val estadoId = estadoIdTexto.toLongOrNull() ?: 1L

                            val pregunta = preguntaEnEdicion
                            if (pregunta != null) {
                                viewModel.actualizarPregunta(
                                    id = pregunta.id,
                                    enunciado = enunciado,
                                    idCategoria = categoriaId,
                                    idDificultad = dificultadId,
                                    idEstado = estadoId,
                                    textosOpciones = opcionesTextos,
                                    indiceCorrecta = indiceCorrecta
                                )
                            }

                            // limpiar y cerrar
                            preguntaEnEdicion = null
                            enunciado = ""
                            categoriaIdTexto = "1"
                            dificultadIdTexto = "1"
                            estadoIdTexto = "1"
                            opcionesTextos = List(4) { "" }
                            indiceCorrecta = 0
                            mostrarDialogoEditar = false
                        }
                    ) {
                        Text("Guardar cambios")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEditar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AdminPreguntasScreenPreview() {
    val navController = rememberNavController()
    AdminPreguntasScreen(navController)
}
