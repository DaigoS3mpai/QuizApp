package com.example.quizapp.ui.viewmodel.menu

import com.example.quizapp.data.remote.FunFactApi
import com.example.quizapp.data.remote.dto.FunFactResponse
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuOpcionesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val api = mockk<FunFactApi>()
    private lateinit var viewModel: MenuOpcionesViewModel

    @Before
    fun setup() {
        viewModel = MenuOpcionesViewModel(api)
    }

    @Test
    fun cargarFunFact_exito_seteaFact() = runTest {
        // ✅ Opción 1: si FunFactResponse es data class con constructor
        // val response = FunFactResponse(text = "Dato curioso")

        // ✅ Opción 2 (más universal): mockear la respuesta
        val response = mockk<FunFactResponse>(relaxed = true)
        io.mockk.every { response.text } returns "Dato curioso"

        coEvery { api.getRandomFact() } returns response

        viewModel.cargarFunFact()
        advanceUntilIdle()

        val state = viewModel.funFactState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Dato curioso", state.fact)
    }

    @Test
    fun cargarFunFact_error_seteaError() = runTest {
        coEvery { api.getRandomFact() } throws RuntimeException("fail")

        viewModel.cargarFunFact()
        advanceUntilIdle()

        val state = viewModel.funFactState.value
        assertFalse(state.isLoading)
        assertEquals("No se pudo cargar el dato curioso", state.error)
    }
}
