package com.example.quizapp.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuInicioSesion(navController: NavHostController) {

    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        buttonsVisible = true
    }
    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // centrado vertical
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo principal
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {

            // Botón para crear cuenta
            Button(
                onClick = { navController.navigate(Route.Registro.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Crear Cuenta", fontSize = 22.sp)
            }

            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {

            // Botón para iniciar sesión
            Button(
                onClick = { navController.navigate(Route.Login.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Iniciar Sesión", fontSize = 22.sp)
            }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuInicioSesionPreview() {
    val navController = rememberNavController()
    MenuInicioSesion(navController)
}
