package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.ui.screen.cateScreen
import com.example.quizapp.ui.screen.loginScreen
import com.example.quizapp.ui.screen.menuPrincipal
import com.example.quizapp.ui.screen.menuScreen
import com.example.quizapp.ui.screen.perfilScreen
import com.example.quizapp.ui.screen.playScreen
import com.example.quizapp.ui.screen.registerScreen
import com.example.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "menuPrincipal") {
                    composable("menuPrincipal") { menuScreen(navController) }
                    composable("registro") { registerScreen(navController) }
                    composable("login") { loginScreen(navController) }
                    composable("MenuOpciones") { menuPrincipal(navController) }
                    composable("Perfil") { perfilScreen(navController) }
                    composable("Jugar") { playScreen(navController) }
                    composable("Categoria") { cateScreen(navController)}
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    QuizAppTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "menuScreen") {
            composable("menuScreen") { menuScreen(navController) }
            composable("registro") { registerScreen(navController) }
            composable("login") { loginScreen(navController) }
            composable("MenuOpciones") { menuPrincipal(navController) }
            composable("Jugar") { playScreen(navController) }
            composable("Categoria") { cateScreen(navController)}
        }
    }
}


