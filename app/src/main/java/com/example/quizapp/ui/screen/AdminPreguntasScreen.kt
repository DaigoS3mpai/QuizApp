package com.example.quizapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.data.pregunta.PreguntaLite
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.AdminPreguntasViewModel
import com.example.quizapp.ui.viewmodel.AdminPreguntasViewModelFactory

@Composable
fun AdminPreguntasScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: AdminPreguntasViewModel =
        viewModel(factory = AdminPreguntasViewModelFactory(context))

    val preguntas by viewModel.preguntas.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val dificultades by viewModel.dificultades.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val detallePregunta by viewModel.detallePregunta.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<Int?>(null) }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var puntajeTexto by remember { mutableStateOf("") }
    var estadoIdTexto by remember { mutableStateOf("1") }
    var categoriaIdTexto by remember { mutableStateOf("1") }
    var dificultadIdTexto by remember { mutableStateOf("1") }
    var opcionesTextos by remember { mutableStateOf(List(4) { "" }) }
    var indiceCorrecta by remember { mutableStateOf(0) }

    // Cuando cargamos detalle de una pregunta para editar
    LaunchedEffect(detallePregunta) {
        detallePregunta?.let { det ->
            editandoId = det.pregunta.id_pregunta
            nombre = det.pregunta.nombre
            puntajeTexto = det.pregunta.puntaje.toString()
            estadoIdTexto = det.pregunta.estado_id_estado.toString()
            categoriaIdTexto = det.pregunta.categoria_id_categoria.toString()
            dificultadIdTexto = det.pregunta.dificultad_id_dificultad.toString()

            val listaOpciones = det.opciones
            opcionesTextos = List(4) { index ->
                listaOpciones.getOrNull(index)?.texto ?: ""
            }
            indiceCorrecta = listaOpciones.indexOfFirst { it.correcta == 1 }.coerceAtLeast(0)

            mostrarDialogo = true
        }
    }

    fun limpiarFormulario() {
        editandoId = null
        nombre = ""
        puntajeTexto = ""
        estadoIdTexto = "1"
        categoriaIdTexto = "1"
        dificultadIdTexto = "1"
        opcionesTextos = List(4) { "" }
        indiceCorrecta = 0
        viewModel.limpiarDetalle()
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total preguntas: ${preguntas.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = {
                    limpiarFormulario()
                    mostrarDialogo = true
                }) {
                    Text("Nueva pregunta")
                }
            }

            if (mensajeError != null) {
                Text(
                    text = "Error: $mensajeError",
                    color = MaterialTheme.colorScheme.error
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(preguntas) { p ->
                    PreguntaItem(
                        pregunta = p,
                        categoriasNombres = { id ->
                            categorias.firstOrNull { it.id_categoria == id }?.nombre_categoria ?: "Cat $id"
                        },
                        dificultadesNombres = { id ->
                            dificultades.firstOrNull { it.id_dificultad == id }?.nombre_dificultad ?: "Dif $id"
                        },
                        estadosNombres = { id ->
                            estados.firstOrNull { it.id_estado == id }?.nombre ?: "Estado $id"
                        },
                        onEditar = {
                            viewModel.cargarDetallePregunta(p.id_pregunta)
                        },
                        onEliminar = {
                            viewModel.eliminarPregunta(p.id_pregunta)
                        }
                    )
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

    if (mostrarDialogo) {
        DialogFormularioPregunta(
            titulo = if (editandoId == null) "Nueva pregunta" else "Editar pregunta",
            nombre = nombre,
            onNombreChange = { nombre = it },
            puntajeTexto = puntajeTexto,
            onPuntajeChange = { puntajeTexto = it },
            estadoIdTexto = estadoIdTexto,
            onEstadoIdChange = { estadoIdTexto = it },
            categoriaIdTexto = categoriaIdTexto,
            onCategoriaIdChange = { categoriaIdTexto = it },
            dificultadIdTexto = dificultadIdTexto,
            onDificultadIdChange = { dificultadIdTexto = it },
            opcionesTextos = opcionesTextos,
            onOpcionChange = { index, value ->
                opcionesTextos = opcionesTextos.toMutableList().also { it[index] = value }
            },
            indiceCorrecta = indiceCorrecta,
            onIndiceCorrectaChange = { indiceCorrecta = it },
            onDismiss = {
                mostrarDialogo = false
                limpiarFormulario()
            },
            onGuardar = {
                val puntaje = puntajeTexto.toIntOrNull() ?: 0
                val estadoId = estadoIdTexto.toIntOrNull() ?: 1
                val categoriaId = categoriaIdTexto.toIntOrNull() ?: 1
                val dificultadId = dificultadIdTexto.toIntOrNull() ?: 1

                if (opcionesTextos.any { it.isBlank() } || nombre.isBlank()) {
                    // Validación básica
                    return@DialogFormularioPregunta
                }

                if (editandoId == null) {
                    viewModel.crearPregunta(
                        nombre = nombre,
                        puntaje = puntaje,
                        estadoId = estadoId,
                        categoriaId = categoriaId,
                        dificultadId = dificultadId,
                        textosOpciones = opcionesTextos,
                        indiceCorrecta = indiceCorrecta
                    )
                } else {
                    viewModel.actualizarPregunta(
                        idPregunta = editandoId!!,
                        nombre = nombre,
                        puntaje = puntaje,
                        estadoId = estadoId,
                        categoriaId = categoriaId,
                        dificultadId = dificultadId,
                        textosOpciones = opcionesTextos,
                        indiceCorrecta = indiceCorrecta
                    )
                }

                mostrarDialogo = false
                limpiarFormulario()
            }
        )
    }
}

@Composable
private fun PreguntaItem(
    pregunta: PreguntaLite,
    categoriasNombres: (Int) -> String,
    dificultadesNombres: (Int) -> String,
    estadosNombres: (Int) -> String,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "#${pregunta.id_pregunta} - ${pregunta.nombre}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Puntaje: ${pregunta.puntaje}")
            Text("Categoría: ${categoriasNombres(pregunta.categoria_id_categoria)}")
            Text("Dificultad: ${dificultadesNombres(pregunta.dificultad_id_dificultad)}")
            Text("Estado: ${estadosNombres(pregunta.estado_id_estado)}")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedButton(onClick = onEditar) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onEliminar, colors = ButtonDefaults.outlinedButtonColors()) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
private fun DialogFormularioPregunta(
    titulo: String,
    nombre: String,
    onNombreChange: (String) -> Unit,
    puntajeTexto: String,
    onPuntajeChange: (String) -> Unit,
    estadoIdTexto: String,
    onEstadoIdChange: (String) -> Unit,
    categoriaIdTexto: String,
    onCategoriaIdChange: (String) -> Unit,
    dificultadIdTexto: String,
    onDificultadIdChange: (String) -> Unit,
    opcionesTextos: List<String>,
    onOpcionChange: (Int, String) -> Unit,
    indiceCorrecta: Int,
    onIndiceCorrectaChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Enunciado") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = puntajeTexto,
                    onValueChange = onPuntajeChange,
                    label = { Text("Puntaje") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estadoIdTexto,
                    onValueChange = onEstadoIdChange,
                    label = { Text("ID Estado (ej. 1)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoriaIdTexto,
                    onValueChange = onCategoriaIdChange,
                    label = { Text("ID Categoría (1=Arte, 2=Deporte, etc.)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dificultadIdTexto,
                    onValueChange = onDificultadIdChange,
                    label = { Text("ID Dificultad (1=Fácil, 2=Media, 3=Difícil)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Opciones (marca cuál es correcta):")

                opcionesTextos.forEachIndexed { index, texto ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = indiceCorrecta == index,
                            onClick = { onIndiceCorrectaChange(index) }
                        )
                        OutlinedTextField(
                            value = texto,
                            onValueChange = { onOpcionChange(index, it) },
                            label = { Text("Opción ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminPreguntasScreenPreview(){
    val navController = rememberNavController()
    AdminPreguntasScreen(navController)
}
