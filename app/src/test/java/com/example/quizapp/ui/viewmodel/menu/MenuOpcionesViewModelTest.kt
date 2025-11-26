package com.example.quizapp.ui.viewmodel.menu

import com.example.quizapp.data.remote.FunFactApi
import com.example.quizapp.data.remote.FunFactApiClient
import com.example.quizapp.data.remote.dto.FunFactResponse // 👈 ajusta el nombre/paquete si es distinto
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuOpcionesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun tearDown() {
        // Limpia los mocks estáticos entre tests
        unmockkAll()
    }

    // ----------------------------------------------------
    // ESTADO INICIAL
    // ----------------------------------------------------
    @Test
    fun `estado inicial tiene fact vacio no cargando y sin error`() = runTest {
        val vm = MenuOpcionesViewModel()
        val state = vm.funFactState.value

        assertEquals("", state.fact)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    // ----------------------------------------------------
    // cargarFunFact - EXITO
    // ----------------------------------------------------
    @Test
    fun `cargarFunFact actualiza fact cuando la api responde ok`() = runTest {
        // 1) Mock del objeto singleton FunFactApiClient
        mockkObject(FunFactApiClient)

        // 2) Mock de la interfaz FunFactApi
        val apiMock = mockk<FunFactApi>()

        // 3) Cuando se acceda a FunFactApiClient.api devolvemos nuestro mock
        every { FunFactApiClient.api } returns apiMock

        // 4) Cuando se llame getRandomFact devolvemos un DTO con texto
        coEvery { apiMock.getRandomFact() } returns FunFactResponse(
            text = "Dato curioso de prueba",
            id = "2"
        )

        val vm = MenuOpcionesViewModel()

        // Act
        vm.cargarFunFact()
        advanceUntilIdle()   // deja terminar las corutinas del viewModelScope

        // Assert
        val state = vm.funFactState.value
        assertFalse(state.isLoading)
        assertEquals("Dato curioso de prueba", state.fact)
        assertNull(state.error)
    }

    // ----------------------------------------------------
    // cargarFunFact - ERROR
    // ----------------------------------------------------
    @Test
    fun `cargarFunFact pone mensaje de error cuando la api lanza excepcion`() = runTest {
        mockkObject(FunFactApiClient)
        val apiMock = mockk<FunFactApi>()
        every { FunFactApiClient.api } returns apiMock

        // Forzamos una excepción en la llamada remota
        coEvery { apiMock.getRandomFact() } throws RuntimeException("fallo de red")

        val vm = MenuOpcionesViewModel()

        vm.cargarFunFact()
        advanceUntilIdle()

        val state = vm.funFactState.value
        assertFalse(state.isLoading)
        assertNull(state.fact.takeIf { it.isNotEmpty() }) // opcional: fact debería seguir vacío
        assertNotNull(state.error)
    }
}
