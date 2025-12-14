package com.example.quizapp.ui.screen.quiz

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import com.example.quizapp.ui.viewmodel.menu.MenuOpcionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOpciones(navController: NavHostController) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val menuViewModel: MenuOpcionesViewModel = viewModel()

    LaunchedEffect(Unit) {
        authViewModel.loadSession()
        menuViewModel.cargarFunFact()
    }

    val currentUser by authViewModel.currentUser.collectAsState()
    val funFactState by menuViewModel.funFactState.collectAsState()

    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { buttonsVisible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val borderGlowColor = if (currentUser.loggedIn)
        Color(0xFF4CAF50).copy(alpha = glowAlpha)
    else Color.Gray.copy(alpha = 0.6f)

    Scaffold(
        topBar = {
            AppTopBar(
                actions = {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(3.dp, borderGlowColor, CircleShape)
                            .shadow(
                                elevation = if (currentUser.loggedIn) 10.dp else 0.dp,
                                shape = CircleShape,
                                ambientColor = borderGlowColor,
                                spotColor = borderGlowColor
                            )
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate(Route.Perfil.path)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val painter = when {
                            currentUser.photo != null -> rememberAsyncImagePainter(currentUser.photo)
                            else -> painterResource(R.drawable.perfil)
                        }

                        Image(
                            painter = painter,
                            contentDescription = "Perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 Logo más pequeño
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicacion",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),   // antes 250.dp
                contentScale = ContentScale.Fit
            )

            // 🔹 Card de dato curioso más compacta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp, max = 180.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00FFC4)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Dato curioso del día",
                        fontSize = 16.sp,
                        color = Color(0xFF0D47A1)
                    )

                    when {
                        funFactState.isLoading -> {
                            Text("Cargando dato curioso...", fontSize = 14.sp)
                        }

                        funFactState.error != null -> {
                            Text(
                                text = funFactState.error ?: "",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }

                        funFactState.fact.isNotBlank() -> {
                            Text(
                                text = funFactState.fact,
                                fontSize = 14.sp
                            )
                        }

                        else -> {
                            Text("No hay datos disponibles", fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    TextButton(onClick = { menuViewModel.cargarFunFact() }) {
                        Text("Otro dato curioso", fontSize = 14.sp)
                    }
                }
            }

            // ⬇️ De aquí para abajo todo igual, solo cambian los espacios

            if (currentUser.isAdmin) {
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(tween(500, delayMillis = 200)),
                    exit = scaleOut(tween(500))
                ) {
                    Button(
                        onClick = { navController.navigate(Route.AdminMenu.path) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FFE0),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Menu Admin", fontSize = 20.sp)
                    }
                }
            }

            if (currentUser.isQuiz) {
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(tween(500, delayMillis = 200)),
                    exit = scaleOut(tween(500))
                ) {
                    Button(
                        onClick = { navController.navigate(Route.QuizMenu.path) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FFE0),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Menu Quiz", fontSize = 18.sp)
                    }
                }
            }

            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(tween(500, delayMillis = 200)),
                exit = scaleOut(tween(500))
            ) {
                Button(
                    onClick = { navController.navigate(Route.FeedbackJugador.path) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Enviar feedback", fontSize = 18.sp)
                }
            }

            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(tween(500, delayMillis = 200)),
                exit = scaleOut(tween(500))
            ) {
                Button(
                    onClick = { navController.navigate(Route.Selecion.path) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Jugar", fontSize = 22.sp)
                }
            }

            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(tween(500, delayMillis = 200)),
                exit = scaleOut(tween(500))
            ) {
                Button(
                    onClick = {
                        authViewModel.logout()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        navController.navigate(Route.Login.path) {
                            popUpTo(Route.MenuOpciones.path) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cerrar Sesión", fontSize = 22.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuPrincipalPreview() {
    val navController = rememberNavController()
    MenuOpciones(navController)
}
