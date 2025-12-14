package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import com.example.quizapp.data.remote.dto.OpcionDto
import com.example.quizapp.data.remote.dto.PreguntaDto
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        val context = mockk<Context>(relaxed = true)
        viewModel = QuizViewModel(context)
    }

    @Test
    fun responder_siTerminado_true_noHaceNada() = runTest {
        setPrivateUiState(
            QuizUiState(
                preguntaActual = null,
                opciones = emptyList(),
                puntaje = 50,
                terminado = true
            )
        )

        val opcion = mockk<OpcionDto>(relaxed = true)
        every { opcion.correcta } returns true

        viewModel.responder(opcion)
        advanceUntilIdle()

        assertEquals(50, viewModel.uiState.value.puntaje)
        assertTrue(viewModel.uiState.value.terminado)
    }

    @Test
    fun responder_incorrecta_marcaPerdiste_yTermina() = runTest {
        // Puntaje base 10, multiplicador 1 por defecto
        val pregunta = mockk<PreguntaDto>(relaxed = true)
        every { pregunta.puntaje } returns 10

        setPrivateUiState(
            QuizUiState(
                preguntaActual = pregunta,
                opciones = emptyList(),
                puntaje = 0,
                terminado = false
            )
        )

        val opcion = mockk<OpcionDto>(relaxed = true)
        every { opcion.correcta } returns false

        viewModel.responder(opcion)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.terminado)
        assertTrue(viewModel.mostrarPerdiste.value)
        assertEquals(0, viewModel.uiState.value.puntaje)
    }

    @Test
    fun responder_correcta_sumaPuntaje_segunMultiplicador() = runTest {
        val pregunta = mockk<PreguntaDto>(relaxed = true)
        every { pregunta.puntaje } returns 10

        // Forzamos multiplicador = 3 (como dificultad difícil)
        setPrivateField("multiplicadorPuntaje", 3)

        setPrivateUiState(
            QuizUiState(
                preguntaActual = pregunta,
                opciones = emptyList(),
                puntaje = 0,
                terminado = false
            )
        )

        val opcion = mockk<OpcionDto>(relaxed = true)
        every { opcion.correcta } returns true

        viewModel.responder(opcion)
        advanceUntilIdle()

        // 10 * 3 = 30
        assertEquals(30, viewModel.uiState.value.puntaje)
        // No necesariamente termina aquí (depende del flujo), pero no debería marcar perdiste
        assertFalse(viewModel.mostrarPerdiste.value)
    }

    // ---------- helpers por reflexión ----------

    private fun setPrivateUiState(state: QuizUiState) {
        val field = QuizViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<QuizUiState>
        flow.value = state
    }

    private fun setPrivateField(fieldName: String, value: Any) {
        val field = QuizViewModel::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(viewModel, value)
    }
}