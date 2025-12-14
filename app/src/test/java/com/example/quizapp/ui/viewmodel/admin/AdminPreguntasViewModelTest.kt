package com.example.quizapp.ui.viewmodel.admin

import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminPreguntasViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repo = mockk<QuizRepository>(relaxed = true)
    private lateinit var viewModel: AdminPreguntasViewModel

    @Before
    fun setup() {
        viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = Dispatchers.Main // el Main lo controla la Rule
        )
    }

    @Test
    fun crearPregunta_conCamposVacios_muestraError_yNoLlamaRepo() = runTest {
        viewModel.crearPregunta(
            enunciado = "",
            idCategoria = 1,
            idDificultad = 1,
            idEstado = 1,
            textosOpciones = listOf("A", "B"),
            indiceCorrecta = 0
        )

        assertEquals(
            "Debes completar enunciado y todas las opciones.",
            viewModel.uiState.value.errorMsg
        )

        coVerify(exactly = 0) { repo.crearPregunta(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun cargarPreguntas_error_muestraMensaje() = runTest {
        coEvery { repo.obtenerPreguntas() } throws RuntimeException("fail")

        viewModel.cargarPreguntas()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMsg)
        assertTrue(viewModel.uiState.value.errorMsg!!.contains("Error al cargar preguntas"))
    }
}
