package com.example.quizapp.ui.viewmodel.admin

import com.example.quizapp.data.remote.dto.PreguntaAdminResponseDto
import com.example.quizapp.data.repository.QuizRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminPreguntasViewModelTest {

    // ----------------------------------------------------
    // ESTADO INICIAL + cargarPreguntas()
    // ----------------------------------------------------
    @Test
    fun `al iniciar carga preguntas correctamente cuando repo responde ok`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        val p1 = mockk<PreguntaAdminResponseDto>()
        val p2 = mockk<PreguntaAdminResponseDto>()

        coEvery { repo.obtenerPreguntas() } returns listOf(p1, p2)

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        // deja que corran las corutinas del init { cargarPreguntas() }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMsg)
        assertEquals(2, state.preguntas.size)
        assertSame(p1, state.preguntas[0])

        coVerify(exactly = 1) { repo.obtenerPreguntas() }
    }

    @Test
    fun `cargarPreguntas guarda mensaje de error cuando repo lanza excepcion`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        coEvery { repo.obtenerPreguntas() } throws RuntimeException("fallo")

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.preguntas.isEmpty())
        assertNotNull(state.errorMsg)
    }

    // ----------------------------------------------------
    // crearPregunta
    // ----------------------------------------------------
    @Test
    fun `crearPregunta con enunciado u opciones vacias setea error y no llama repo`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        // para el init
        coEvery { repo.obtenerPreguntas() } returns emptyList()

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )
        advanceUntilIdle()

        viewModel.crearPregunta(
            enunciado = "",
            idCategoria = 1L,
            idDificultad = 1L,
            idEstado = 1L,
            textosOpciones = listOf("A", "B", "C", "D"),
            indiceCorrecta = 0
        )

        val state = viewModel.uiState.value
        assertEquals("Debes completar enunciado y todas las opciones.", state.errorMsg)

        coVerify(exactly = 0) {
            repo.crearPregunta(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `crearPregunta valida datos y llama repo y recarga lista`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        // init
        coEvery { repo.obtenerPreguntas() } returns emptyList()

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )
        advanceUntilIdle()

        viewModel.crearPregunta(
            enunciado = "Capital de Francia",
            idCategoria = 1L,
            idDificultad = 2L,
            idEstado = 1L,
            textosOpciones = listOf("París", "Roma", "Madrid", "Berlín"),
            indiceCorrecta = 0
        )

        advanceUntilIdle()

        coVerify(exactly = 1) {
            repo.crearPregunta(
                enunciado = "Capital de Francia",
                idCategoria = 1L,
                idDificultad = 2L,
                idEstado = 1L,
                textosOpciones = listOf("París", "Roma", "Madrid", "Berlín"),
                indiceCorrecta = 0
            )
        }

        // obtenerPreguntas se llama en init y después de crearPregunta
        coVerify(atLeast = 2) { repo.obtenerPreguntas() }
    }

    // ----------------------------------------------------
    // actualizarPregunta
    // ----------------------------------------------------
    @Test
    fun `actualizarPregunta con datos invalidos setea error y no llama repo`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        coEvery { repo.obtenerPreguntas() } returns emptyList()

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )
        advanceUntilIdle()

        viewModel.actualizarPregunta(
            id = 10L,
            enunciado = "",
            idCategoria = 1L,
            idDificultad = 1L,
            idEstado = 1L,
            textosOpciones = listOf("A", "B", "C", "D"),
            indiceCorrecta = 1
        )

        val state = viewModel.uiState.value
        assertEquals("Debes completar enunciado y todas las opciones.", state.errorMsg)

        coVerify(exactly = 0) {
            repo.actualizarPregunta(any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `actualizarPregunta valida datos y llama repo y recarga lista`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        coEvery { repo.obtenerPreguntas() } returns emptyList()

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )
        advanceUntilIdle()

        viewModel.actualizarPregunta(
            id = 15L,
            enunciado = "Nueva pregunta",
            idCategoria = 2L,
            idDificultad = 3L,
            idEstado = 1L,
            textosOpciones = listOf("Op1", "Op2", "Op3", "Op4"),
            indiceCorrecta = 2
        )

        advanceUntilIdle()

        coVerify(exactly = 1) {
            repo.actualizarPregunta(
                id = 15L,
                enunciado = "Nueva pregunta",
                idCategoria = 2L,
                idDificultad = 3L,
                idEstado = 1L,
                textosOpciones = listOf("Op1", "Op2", "Op3", "Op4"),
                indiceCorrecta = 2
            )
        }

        coVerify(atLeast = 2) { repo.obtenerPreguntas() }
    }

    // ----------------------------------------------------
    // eliminarPregunta
    // ----------------------------------------------------
    @Test
    fun `eliminarPregunta llama repo y luego recarga lista`() = runTest {
        val repo: QuizRepository = mockk(relaxed = true)
        val dispatcher = StandardTestDispatcher(testScheduler)

        coEvery { repo.obtenerPreguntas() } returns emptyList()

        val viewModel = AdminPreguntasViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )
        advanceUntilIdle()

        viewModel.eliminarPregunta(99L)
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.eliminarPregunta(99L) }
        coVerify(atLeast = 2) { repo.obtenerPreguntas() }
    }
}
