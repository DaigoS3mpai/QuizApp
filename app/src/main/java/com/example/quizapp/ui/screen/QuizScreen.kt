package com.example.quizapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.QuizViewModel
import com.example.quizapp.ui.viewmodel.QuizViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(navController: NavHostController, dificultadId: Int, categoriaId: Int) {
    val context = LocalContext.current
    val viewModel: QuizViewModel = viewModel(factory = QuizViewModelFactory(context))
    val state by viewModel.uiState.collectAsState()
    val mostrarPerdiste by viewModel.mostrarPerdiste.collectAsState()

    var mostrarFelicidades by remember { mutableStateOf(false) }

    // ✅ Cargar preguntas una sola vez
    LaunchedEffect(Unit) {
        viewModel.cargarPreguntas(dificultadId = dificultadId, categoriaId = categoriaId)
    }

    // 🕓 Progreso circular
    val totalTiempo = remember { mutableStateOf(30f) }
    val progreso = animateFloatAsState(
        targetValue = if (totalTiempo.value > 0) state.tiempoRestante / totalTiempo.value else 0f,
        animationSpec = tween(1000),
        label = "progresoCircular"
    )

    Scaffold(topBar = { AppTopBar() }) { padding ->

        // ❌ Cuadro “Perdiste”
        if (mostrarPerdiste) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("❌ Perdiste") },
                text = { Text("Puntaje total: ${state.puntaje}") },
                confirmButton = {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDF3C3C),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Volver al menú")
                    }
                }
            )
        }

        // 🎉 Cuadro “Felicidades”
        if (mostrarFelicidades) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("🎉 ¡Felicidades!") },
                text = { Text("Completaste el quiz con ${state.puntaje} puntos 🏆") },
                confirmButton = {
                    Button(onClick = {
                        mostrarFelicidades = false
                        navController.popBackStack()
                    }) {
                        Text("Volver al menú")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 🕒 Temporizador circular
            if (!state.terminado && state.preguntaActual != null) {
                totalTiempo.value = (state.tiempoRestante.takeIf { it > 0 } ?: 30).toFloat()

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val strokeWidth = 8f
                        drawArc(
                            color = Color.LightGray,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = if (state.tiempoRestante <= 5) Color.Red else Color(0xFF0077CC),
                            startAngle = -90f,
                            sweepAngle = 360f * progreso.value,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${state.tiempoRestante}s",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.tiempoRestante <= 5) Color.Red else Color.Black
                    )
                }
            }

            when {
                state.preguntaActual == null && !state.terminado -> {
                    CircularProgressIndicator(color = Color.Black)
                    Text("Cargando pregunta...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                state.terminado && state.preguntaActual == null -> {
                    Text(
                        text = "⚠️ No se encontraron preguntas disponibles.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        )
                    ) { Text("Volver", fontSize = 20.sp) }
                }

                else -> {
                    AnimatedContent(
                        targetState = state.preguntaActual,
                        transitionSpec = {
                            slideInHorizontally(animationSpec = tween(400)) { it } + fadeIn(animationSpec = tween(400)) togetherWith
                                    slideOutHorizontally(animationSpec = tween(400)) { -it } + fadeOut(animationSpec = tween(400))
                        },
                        label = "PreguntaAnimacion"
                    ) { pregunta ->
                        pregunta?.let {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                val imagenBitmap = state.imagenBitmap
                                if (imagenBitmap != null) {
                                    val alphaAnim = remember { Animatable(0f) }
                                    LaunchedEffect(imagenBitmap) {
                                        alphaAnim.snapTo(0f)
                                        alphaAnim.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(600)
                                        )
                                    }
                                    Image(
                                        bitmap = imagenBitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .alpha(alphaAnim.value)
                                            .background(Color.White, shape = RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // ❓ Texto de pregunta
                                Text(
                                    text = it.nombre,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // 🔀 Opciones aleatorias
                                val opcionesAleatorias = remember(it.id_pregunta) {
                                    state.opciones.shuffled()
                                }

                                opcionesAleatorias.forEach { opcion ->
                                    Button(
                                        onClick = {
                                            if (opcion.correcta == 0) {
                                                // ❌ Mostrar “Perdiste”
                                                // (el ViewModel también lo marca internamente)
                                            } else if (state.preguntaIndex >= state.totalPreguntas - 1) {
                                                mostrarFelicidades = true
                                            }
                                            viewModel.responder(opcion)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF58B956),
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(opcion.texto, fontSize = 18.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Puntaje: ${state.puntaje}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    val navController = rememberNavController()
    QuizScreen(navController = navController, dificultadId = 1, categoriaId = 1)
}
