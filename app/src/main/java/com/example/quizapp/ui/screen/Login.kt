package com.example.quizapp.ui.screen

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.R
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Campo usuario
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            // Botón iniciar sesión
            Button(
                onClick = { navController.navigate(Route.MenuOpciones.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Iniciar Sesión", fontSize = 18.sp)
            }

            // Botón volver
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    Login(navController)
}
