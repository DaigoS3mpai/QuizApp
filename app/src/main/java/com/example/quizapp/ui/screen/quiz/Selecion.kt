package com.example.quizapp.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.R
import com.example.quizapp.ui.component.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selecion(navController: NavHostController) {
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { buttonsVisible = true }

    Scaffold(topBar = { AppTopBar() }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🖼 Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )

            val dificultades = listOf(
                "Fácil" to 1,
                "Normal" to 2,
                "Difícil" to 3
            )

            // 🎮 Botones de dificultad
            dificultades.forEachIndexed { index, (nombre, idDificultad) ->
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = scaleIn(animationSpec = tween(500, delayMillis = index * 100)),
                    exit = scaleOut(animationSpec = tween(300))
                ) {
                    Button(
                        onClick = { navController.navigate("Categoria/$idDificultad") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF58B956),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(nombre, fontSize = 25.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔙 Botón volver
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = scaleIn(animationSpec = tween(500, delayMillis = 400)),
                exit = scaleOut(animationSpec = tween(300))
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver", fontSize = 25.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelecionPreview() {
    val navController = rememberNavController()
    Selecion(navController)
}
