package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import com.example.quizapp.data.remote.dto.OpcionDto
import com.example.quizapp.data.remote.dto.PreguntaDto
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: QuizViewModel
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        viewModel = QuizViewModel(mockContext)
    }

    // ----------------------------------------------------
    // ESTADO INICIAL
    // ----------------------------------------------------

    @Test
    fun `estado inicial del quiz esta vacio y no terminado`() {
        val state = viewModel.uiState.value

        assertNull(state.preguntaActual)
        assertTrue(state.opciones.isEmpty())
        assertEquals(0, state.puntaje)
        assertEquals(0, state.preguntaIndex)
        assertEquals(0, state.totalPreguntas)
        assertEquals(0, state.tiempoRestante)
        assertFalse(state.terminado)
    }

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------

    private fun givenPreguntaEnCurso(
        puntajePregunta: Int = 10,
        correcta: Boolean = true
    ): OpcionDto {

        // CREAMOS DTO REALES, NO MOCKS
        val pregunta = PreguntaDto(
            id = 1,
            enunciado = "¿2+2?",
            puntaje = puntajePregunta,
            opciones = emptyList()
        )

        val opcion = OpcionDto(
            id = 1,
            texto = "4",
            correcta = correcta
        )

        // Inyectar estado con reflexión
        val field = QuizViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(viewModel) as MutableStateFlow<QuizUiState>

        flow.value = QuizUiState(
            preguntaActual = pregunta,
            opciones = listOf(opcion),
            tiempoRestante = 10,
            puntaje = 0,
            preguntaIndex = 0,
            totalPreguntas = 1,
            terminado = false
        )

        return opcion
    }

    private fun setMultiplicadorPuntaje(value: Int) {
        val field = QuizViewModel::class.java.getDeclaredField("multiplicadorPuntaje")
        field.isAccessible = true
        field.set(viewModel, value)
    }

    // ----------------------------------------------------
    // TEST RESPONDER
    // ----------------------------------------------------

    @Test
    fun `responder incorrecto termina el quiz y no suma puntaje`() = runTest {
        val opcion = givenPreguntaEnCurso(correcta = false)

        viewModel.responder(opcion)

        val state = viewModel.uiState.value

        assertTrue(state.terminado)
        assertEquals(0, state.puntaje)
    }

    @Test
    fun `responder correcta suma puntaje usando multiplicador`() {
        setMultiplicadorPuntaje(3)
        val opcion = givenPreguntaEnCurso(puntajePregunta = 10, correcta = true)

        viewModel.responder(opcion)

        val state = viewModel.uiState.value
        assertEquals(30, state.puntaje)
        assertFalse(state.terminado)
    }
}
