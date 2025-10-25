package com.example.quizapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.quizapp.ui.component.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaFacil(navController: NavHostController) {
    var buttonsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        buttonsVisible = true
    }
    Scaffold(
        topBar = {
            AppTopBar()
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
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo Aplicacion",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            Button(
                onClick = { navController.navigate("") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Arte", fontSize = 25.sp)
            }

            }
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            Button(
                onClick = { navController.navigate("") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Deporte", fontSize = 25.sp)
            }

            }
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            Button(
                onClick = { navController.navigate("") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Historia", fontSize = 25.sp)
            }

            }
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            Button(
                onClick = { navController.navigate("") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Cine", fontSize = 25.sp)
            }

            }
            AnimatedVisibility(
                visible = buttonsVisible,

                enter = scaleIn(animationSpec = tween(500, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(500))
            ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF58B956),
                    contentColor = Color.Black
                )
            ) {
                Text("Volver", fontSize = 25.sp)
            }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun cateScreenPreview() {
    val navController = rememberNavController()
    CategoriaFacil(navController)
}