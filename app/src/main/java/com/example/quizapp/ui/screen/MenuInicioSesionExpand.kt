package com.example.quizapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.quizapp.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuInicioSesionExpand(navController: NavHostController) {
    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo Aplicación",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop
                )
            }


            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(Route.Registro.path) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Crear Cuenta", fontSize = 25.sp)
                }

                Button(
                    onClick = { navController.navigate(Route.Login.path) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Iniciar Sesión", fontSize = 25.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
fun menuInicioSesionExpandPreview() {
    val navController = rememberNavController()
    MenuInicioSesionExpand(navController)
}