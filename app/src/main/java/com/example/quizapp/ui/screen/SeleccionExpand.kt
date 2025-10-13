package com.example.quizapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun SeleccionExpand(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar()
        }
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
                    contentDescription = "Logo Aplicacion",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )
            }


            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("${Route.Categoria.path}?dificultad=facil") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Facil", fontSize = 25.sp)
                }

                Button(
                    onClick = { navController.navigate("${Route.Categoria.path}?dificultad=medio") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Medio", fontSize = 25.sp)
                }

                Button(
                    onClick = { navController.navigate("${Route.Categoria.path}?dificultad=dificil") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Dificil", fontSize = 25.sp)
                }

                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58B956),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(70.dp)
                ) {
                    Text("Volver", fontSize = 25.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
fun seleccionExpandPreview() {
    val navController = rememberNavController()
    SeleccionExpand(navController)
}