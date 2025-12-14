package com.example.quizapp

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst


@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    // Regla de Jetpack Compose para lanzar MainActivity
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun pantalla_inicial_muestra_alguna_opcion_de_iniciar() {
        composeTestRule
            .onAllNodesWithText("Iniciar", substring = true)
            .onFirst()                 // 👈 tomamos el primero de la lista
            .assertIsDisplayed()
    }
}