package com.example.quizapp.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.navegation.Route
import com.example.quizapp.ui.component.AppTopBar

@Composable
fun QuizMenuScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF87CEEB))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Panel de Quiz",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = { navController.navigate(Route.AdminPreguntas.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFE0),
                    contentColor = Color.Black
                ))
            {
                Text("Administrar preguntas")
            }

            Button(onClick = { navController.navigate(Route.AdminFeedback.path) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFE0),
                    contentColor = Color.Black
                )) {
                Text("Ver feedback")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FFE0),
                    contentColor = Color.Black
                )) {
                Text("Volver")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun QuizMenuScreenPreview(){
    val navController = rememberNavController()
    QuizMenuScreen(navController)
}