package com.example.quizapp.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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

    // ✅ Cargar preguntas solo una vez
    LaunchedEffect(Unit) {
        viewModel.cargarPreguntas(dificultadId = dificultadId, categoriaId = categoriaId)
    }

    // 🕓 Progreso circular del temporizador
    val totalTiempo = remember { mutableStateOf(30f) }
    val progreso = animateFloatAsState(
        targetValue = if (totalTiempo.value > 0) state.tiempoRestante / totalTiempo.value else 0f,
        animationSpec = tween(1000),
        label = "progresoCircular"
    )

    Scaffold(topBar = { AppTopBar() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 🕒 Círculo de temporizador
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

            // 🌀 Estados: carga / error / quiz activo
            when {
                state.preguntaActual == null && !state.terminado -> {
                    Spacer(modifier = Modifier.height(100.dp))
                    CircularProgressIndicator(color = Color.Black)
                    Text(
                        text = "Cargando pregunta...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                state.terminado && state.preguntaActual == null -> {
                    Spacer(modifier = Modifier.height(100.dp))
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
                    ) {
                        Text("Volver", fontSize = 20.sp)
                    }
                }

                else -> {
                    // 🎞 Transición animada entre preguntas
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
                                // 🖼 Imagen con fade-in y fix del smart cast
                                val imagenBitmap = state.imagenBitmap
                                if (imagenBitmap != null) {
                                    var alphaAnim by remember { mutableStateOf(0f) }

                                    // Animación de entrada
                                    LaunchedEffect(imagenBitmap) {
                                        alphaAnim = 0f
                                        animate(
                                            initialValue = 0f,
                                            targetValue = 1f,
                                            animationSpec = tween(600)
                                        ) { value, _ -> alphaAnim = value }
                                    }

                                    Image(
                                        bitmap = imagenBitmap.asImageBitmap(),
                                        contentDescription = "Imagen de la pregunta",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .alpha(alphaAnim)
                                            .background(Color.White, shape = RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                            .background(Color.White, shape = RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Cargando imagen...",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // ❓ Texto de la pregunta
                                Text(
                                    text = it.nombre,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // 🧠 Opciones
                                state.opciones.forEach { opcion ->
                                    Button(
                                        onClick = { viewModel.responder(opcion) },
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

                                // 🏆 Puntaje
                                Text(
                                    "Puntaje: ${state.puntaje}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // 🎉 Fin del quiz
                                if (state.terminado && state.preguntaIndex >= state.totalPreguntas - 1) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "🎉 ¡Has completado el quiz!",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Blue
                                    )
                                    Button(
                                        onClick = { navController.popBackStack() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF58B956),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text("Volver", fontSize = 20.sp)
                                    }
                                }
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
