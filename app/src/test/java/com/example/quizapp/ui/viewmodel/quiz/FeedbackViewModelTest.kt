package com.example.quizapp.ui.viewmodel.quiz

import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import com.example.quizapp.data.repository.FeedbackRepository
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun cargarMisFeedback_exito_actualizaListaYlimpiaError() = runTest {
        val repo = mockk<FeedbackRepository>()
        val vm = FeedbackViewModel(repo = repo, ioDispatcher = testDispatcher)

        val userId = 1L
        val item = mockk<FeedbackResponseDto>(relaxed = true)

        coEvery { repo.listarPorUsuario(userId) } returns listOf(item)

        vm.cargarMisFeedback(userId)
        advanceUntilIdle()

        assertEquals(1, vm.misFeedback.value.size)
        assertNull(vm.mensajeError.value)
    }

    @Test
    fun cargarMisFeedback_error_seteaMensajeError() = runTest {
        val repo = mockk<FeedbackRepository>()
        val vm = FeedbackViewModel(repo = repo, ioDispatcher = testDispatcher)

        val userId = 2L
        coEvery { repo.listarPorUsuario(userId) } throws RuntimeException("fail")

        vm.cargarMisFeedback(userId)
        advanceUntilIdle()

        assertTrue(vm.misFeedback.value.isEmpty())
        assertEquals("Error al cargar feedback: fail", vm.mensajeError.value)
    }

    @Test
    fun enviarFeedback_exito_llamaEnviarYrecargaLista_yEnviandoVuelveFalse() = runTest {
        val repo = mockk<FeedbackRepository>(relaxed = true)
        val vm = FeedbackViewModel(repo = repo, ioDispatcher = testDispatcher)

        val userId = 3L
        val item = mockk<FeedbackResponseDto>(relaxed = true)

        // cuando recarga
        coEvery { repo.listarPorUsuario(userId) } returns listOf(item)

        vm.enviarFeedback(userId, "hola", "SUGERENCIA", "APP")
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.enviarFeedback(userId, "hola", "SUGERENCIA", "APP") }
        coVerify(atLeast = 1) { repo.listarPorUsuario(userId) }

        assertFalse(vm.enviando.value)
        assertNull(vm.mensajeError.value)
        assertEquals(1, vm.misFeedback.value.size)
    }

    @Test
    fun enviarFeedback_error_seteaMensajeError_yEnviandoVuelveFalse() = runTest {
        val repo = mockk<FeedbackRepository>(relaxed = true)
        val vm = FeedbackViewModel(repo = repo, ioDispatcher = testDispatcher)

        val userId = 4L
        coEvery { repo.enviarFeedback(any(), any(), any(), any()) } throws RuntimeException("fail")

        vm.enviarFeedback(userId, "x", "BUG", "APP")
        advanceUntilIdle()

        assertFalse(vm.enviando.value)
        assertEquals("Error al enviar feedback: fail", vm.mensajeError.value)
    }
}