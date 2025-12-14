package com.example.quizapp.ui.screen.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.quiz.QuizViewModel
import com.example.quizapp.ui.viewmodel.quiz.QuizViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    navController: NavHostController,
    dificultadId: Long,
    categoriaId: Long
) {
    val context = LocalContext.current
    val viewModel: QuizViewModel = viewModel(factory = QuizViewModelFactory(context))

    val state by viewModel.uiState.collectAsState()
    val mostrarPerdiste by viewModel.mostrarPerdiste.collectAsState()

    var mostrarFelicidades by remember { mutableStateOf(false) }

    // ⏱ para que el círculo use el tiempo inicial real de la pregunta (30/20/10)
    // Tiempo total por pregunta según la dificultad
    val tiempoInicialPorPregunta = remember(dificultadId) {
        when (dificultadId) {
            1L -> 30   // Fácil
            2L -> 20   // Normal
            3L -> 10   // Difícil
            else -> 30
        }
    }


    // Iniciar quiz al entrar
    LaunchedEffect(Unit) {
        viewModel.iniciarQuiz(dificultadId, categoriaId)
    }

    // Barra circular del tiempo (progreso relativo al tiempo inicial de esa pregunta)
    val progreso = animateFloatAsState(
        targetValue = if (state.tiempoRestante > 0 && tiempoInicialPorPregunta > 0) {
            state.tiempoRestante / tiempoInicialPorPregunta.toFloat()
        } else 0f,
        animationSpec = tween(500),
        label = "animTiempo"
    )

    Scaffold(topBar = { AppTopBar() }) { padding ->

        // ❌ P E R D I S T E
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
                    ) { Text("Volver al menú") }
                }
            )
        }

        // 🎉 F E L I C I D A D E S
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TEMPORIZADOR CIRCULAR
            if (!state.terminado && state.preguntaActual != null) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val strokeWidth = 10f
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
                        "${state.tiempoRestante}s",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when {
                // LOADING
                state.preguntaActual == null && !state.terminado -> {
                    CircularProgressIndicator(color = Color.Black)
                    Text("Cargando pregunta…", fontWeight = FontWeight.Bold)
                }

                // SIN PREGUNTAS
                state.terminado && state.preguntaActual == null -> {
                    Text(
                        "⚠️ No se encontraron preguntas.",
                        color = Color.Red,
                        fontSize = 22.sp
                    )
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }

                // TODO OK → MOSTRAR PREGUNTA
                else -> {
                    AnimatedContent(
                        targetState = state.preguntaActual,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        },
                        label = "preguntaAnim"
                    ) { pregunta ->
                        pregunta?.let { p ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {

                                // ❓ TEXTO DE LA PREGUNTA
                                Text(
                                    text = p.enunciado,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // 🔀 OPCIONES:
                                // ⚠️ IMPORTANTE: usar state.opciones (ya viene mezclado del ViewModel)
                                state.opciones.forEach { opcion ->
                                    Button(
                                        onClick = {
                                            if (!opcion.correcta) {
                                                // incorrecta -> perdiste
                                            } else if (state.preguntaIndex >= state.totalPreguntas - 1) {
                                                mostrarFelicidades = true
                                            }

                                            viewModel.responder(opcion)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF58B956),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(
                                            opcion.texto,
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                Spacer(Modifier.height(12.dp))
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
    QuizScreen(navController, dificultadId = 1, categoriaId = 1)
}
