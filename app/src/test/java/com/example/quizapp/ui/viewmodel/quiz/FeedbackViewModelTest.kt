package com.example.quizapp.ui.viewmodel.quiz

import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import com.example.quizapp.data.repository.FeedbackRepository
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
class FeedbackViewModelTest {

    // ----------------------------------------------------
    // ESTADO INICIAL
    // ----------------------------------------------------
    @Test
    fun `estado inicial es lista vacia, no enviando y sin error`() = runTest {
        val repo: FeedbackRepository = mockk()
        // Dispatcher de prueba ligado al testScheduler de este runTest
        val dispatcher = StandardTestDispatcher(testScheduler)

        val viewModel = FeedbackViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        assertTrue(viewModel.misFeedback.value.isEmpty())
        assertFalse(viewModel.enviando.value)
        assertNull(viewModel.mensajeError.value)
    }

    // ----------------------------------------------------
    // cargarMisFeedback
    // ----------------------------------------------------
    @Test
    fun `cargarMisFeedback actualiza lista cuando repo responde ok`() = runTest {
        val repo: FeedbackRepository = mockk()
        val dispatcher = StandardTestDispatcher(testScheduler)

        val userId = 1L
        val f1 = mockk<FeedbackResponseDto>()
        val f2 = mockk<FeedbackResponseDto>()

        coEvery { repo.listarPorUsuario(userId) } returns listOf(f1, f2)

        val viewModel = FeedbackViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        viewModel.cargarMisFeedback(userId)
        advanceUntilIdle()  // dejamos que terminen las corrutinas

        val lista = viewModel.misFeedback.value
        assertEquals(2, lista.size)
        assertSame(f1, lista[0])
        assertNull(viewModel.mensajeError.value)

        coVerify(exactly = 1) { repo.listarPorUsuario(userId) }
    }

    @Test
    fun `cargarMisFeedback guarda mensaje de error cuando repo lanza excepcion`() = runTest {
        val repo: FeedbackRepository = mockk()
        val dispatcher = StandardTestDispatcher(testScheduler)

        val userId = 1L

        coEvery { repo.listarPorUsuario(userId) } throws RuntimeException("fallo")

        val viewModel = FeedbackViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        viewModel.cargarMisFeedback(userId)
        advanceUntilIdle()

        assertTrue(viewModel.misFeedback.value.isEmpty())
        assertNotNull(viewModel.mensajeError.value)
    }

    // ----------------------------------------------------
    // enviarFeedback
    // ----------------------------------------------------
    @Test
    fun `enviarFeedback marca enviando, llama repo y recarga lista`() = runTest {
        val repo: FeedbackRepository = mockk()
        val dispatcher = StandardTestDispatcher(testScheduler)

        val userId = 5L
        val mensaje = "Me gusto el quiz"
        val tipo = "SUGERENCIA"
        val destino = "APP"

        // 👉 Creamos un mock de la respuesta
        val fbMock = mockk<FeedbackResponseDto>()

        // 👉 Hacemos que enviarFeedback devuelva ese mock (NO Unit)
        coEvery { repo.enviarFeedback(userId, mensaje, tipo, destino) } returns fbMock

        // 👉 Y que al listar devuelva una lista con ese feedback
        coEvery { repo.listarPorUsuario(userId) } returns listOf(fbMock)

        val viewModel = FeedbackViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        viewModel.enviarFeedback(userId, mensaje, tipo, destino)

        // Dejamos correr las corrutinas
        advanceUntilIdle()

        assertFalse(viewModel.enviando.value)
        assertEquals(1, viewModel.misFeedback.value.size)
        assertNull(viewModel.mensajeError.value)

        coVerify(exactly = 1) {
            repo.enviarFeedback(userId, mensaje, tipo, destino)
            repo.listarPorUsuario(userId)
        }
    }


    @Test
    fun `enviarFeedback guarda mensaje de error cuando repo lanza excepcion`() = runTest {
        val repo: FeedbackRepository = mockk()
        val dispatcher = StandardTestDispatcher(testScheduler)

        val userId = 7L

        coEvery { repo.enviarFeedback(userId, any(), any(), any()) } throws RuntimeException("boom")

        val viewModel = FeedbackViewModel(
            repo = repo,
            ioDispatcher = dispatcher
        )

        viewModel.enviarFeedback(userId, "hola", "TIPO", "DEST")
        advanceUntilIdle()

        assertFalse(viewModel.enviando.value)
        assertNotNull(viewModel.mensajeError.value)

        coVerify(exactly = 1) {
            repo.enviarFeedback(userId, any(), any(), any())
        }
    }
}
