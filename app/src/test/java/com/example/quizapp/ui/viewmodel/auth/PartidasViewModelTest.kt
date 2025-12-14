package com.example.quizapp.ui.viewmodel.auth

import android.util.Log
import com.example.quizapp.data.remote.dto.PartidaDto
import com.example.quizapp.data.repository.GameRepository
import com.example.quizapp.ui.viewmodel.Auth.PartidasViewModel
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PartidasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: GameRepository

    @Before
    fun setup() {
        mockkStatic(Log::class)

        // Tipado explícito para evitar "Cannot infer type parameter T"
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.d(any<String>(), any<String>(), any<Throwable>()) } returns 0

        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0

        repo = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun cargarPartidasUsuario_idInvalido_noHaceNada_noLlamaRepo_yNoCambiaEstado() = runTest {
        val vm = PartidasViewModel(repo)

        val before = vm.uiState.value

        vm.cargarPartidasUsuario(0L)
        vm.cargarPartidasUsuario(-1L)

        coVerify(exactly = 0) { repo.obtenerHistorial(any<Long>()) }

        val after = vm.uiState.value
        assertEquals(before, after)
        assertFalse(after.isLoading)
        assertNull(after.error)
        assertTrue(after.partidas.isEmpty())
    }

    @Test
    fun cargarPartidasUsuario_exito_filtraPorUsuario_yActualizaUiState() = runTest {
        val vm = PartidasViewModel(repo)
        val userId = 5L

        val dtoUser = PartidaDto(
            id = 10L,
            usuarioId = userId,
            categoria = "Historia",
            dificultad = "Media",
            fechaInicio = "2025-12-01",
            fechaFin = "2025-12-01",
            puntajeFinal = 80,
            estado = "FINALIZADA"
        )

        val dtoOtroUser = PartidaDto(
            id = 11L,
            usuarioId = 999L,
            categoria = "Ciencia",
            dificultad = "Fácil",
            fechaInicio = "2025-12-02",
            fechaFin = null,
            puntajeFinal = 10,
            estado = "ABANDONADA"
        )

        coEvery { repo.obtenerHistorial(userId) } returns listOf(dtoUser, dtoOtroUser)

        vm.cargarPartidasUsuario(userId)
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.obtenerHistorial(userId) }

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)

        // Debe filtrar y dejar solo las del usuario
        assertEquals(1, state.partidas.size)
        assertEquals(10L, state.partidas.first().id)
    }

    @Test
    fun cargarPartidasUsuario_error_noCrashea_actualizaError_yPartidasVacias() = runTest {
        val vm = PartidasViewModel(repo)
        val userId = 7L

        coEvery { repo.obtenerHistorial(userId) } throws RuntimeException("fail")

        vm.cargarPartidasUsuario(userId)
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.obtenerHistorial(userId) }

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.partidas.isEmpty())
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Error al cargar partidas"))
        assertTrue(state.error!!.contains("fail"))
    }
}
