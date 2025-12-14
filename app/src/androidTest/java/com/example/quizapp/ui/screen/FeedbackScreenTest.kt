package com.example.quizapp.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quizapp.ui.screen.quiz.FeedbackScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedbackScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun feedbackScreen_muestra_componentes_principales() {
        // Montamos SOLO la FeedbackScreen
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                FeedbackScreen(navController = navController)
            }
        }

        // Campo de texto principal
        composeTestRule.onNodeWithText("Mensaje")
            .assertIsDisplayed()

        // Botón de enviar feedback (estado inicial: "Enviar feedback")
        composeTestRule.onNodeWithText("Enviar feedback")
            .assertIsDisplayed()

        // Sección de feedbacks enviados
        composeTestRule.onNodeWithText("Mis feedback enviados:")
            .assertIsDisplayed()

        // Botón de volver
        composeTestRule.onNodeWithText("Volver")
            .assertIsDisplayed()
    }
}