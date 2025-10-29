package com.example.quizapp.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.quizapp.ui.viewmodel.AuthViewModel
import com.example.quizapp.ui.viewmodel.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOpciones(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    //Cargar sesión
    LaunchedEffect(Unit) {
        viewModel.loadSession()
    }

    val currentUser by viewModel.currentUser.collectAsState()
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { buttonsVisible = true }

    //Animación de brillo pulsante
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
                    //Imagen de perfil con efecto "glow"
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Logo principal
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicacion",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )

            //Botón JUGAR
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
                Button(
                    onClick = { navController.navigate(Route.Selecion.path) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Jugar", fontSize = 25.sp)
                }
            }

            //Botón CERRAR SESIÓN
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500, delayMillis = 200)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
                Button(
                    onClick = {
                        viewModel.logout()
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
                    Text("Cerrar Sesión", fontSize = 25.sp)
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
