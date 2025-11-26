package com.example.quizapp.ui.screen

import com.example.quizapp.ui.screen.quiz.MenuOpciones
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuOpcionesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun menu_muestra_botones_principales() {
        // Montamos SOLO la pantalla de menú
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                MenuOpciones(navController = navController)
            }
        }

        // Verificamos que los textos importantes aparecen
        composeTestRule.onNodeWithText("Enviar feedback")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Jugar")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Cerrar Sesión")
            .assertIsDisplayed()
    }
}
