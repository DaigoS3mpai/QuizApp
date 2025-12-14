package com.example.quizapp.ui.viewmodel.admin

import com.example.quizapp.data.repository.AuthRepository
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminJugadoresViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>(relaxed = true)
    private lateinit var viewModel: AdminJugadoresViewModel

    @Before
    fun setup() {
        viewModel = AdminJugadoresViewModel(repo)
    }

    @Test
    fun cargarUsuarios_error_muestraMensaje() = runBlocking {
        coEvery { repo.obtenerTodosLosUsuarios() } throws RuntimeException("fail")

        val vm = AdminJugadoresViewModel(repo)

        vm.cargarUsuarios()

        // ✅ esperar a que el IO termine y setee error
        withTimeout(3000) {
            while (vm.error.value == null) {
                kotlinx.coroutines.delay(10)
            }
        }

        assertNotNull(vm.error.value)
        assertTrue(vm.error.value!!.contains("Error", ignoreCase = true))
    }



    @Test
    fun eliminarJugador_error_muestraMensaje() = runBlocking {
        coEvery { repo.eliminarUsuario(any()) } throws RuntimeException("fail")

        val vm = AdminJugadoresViewModel(repo)

        vm.eliminarJugador(1)

        withTimeout(3000) {
            while (vm.error.value == null) {
                kotlinx.coroutines.delay(10)
            }
        }

        assertNotNull(vm.error.value)
        assertTrue(vm.error.value!!.contains("Error", ignoreCase = true))
    }

    @Test
    fun actualizarPuntaje_error_muestraMensaje() = runBlocking {
        coEvery { repo.actualizarPuntajesAdmin(any(), any(), any()) } throws RuntimeException("fail")

        val vm = AdminJugadoresViewModel(repo)

        vm.actualizarPuntaje(1, 10, 20)

        withTimeout(3000) {
            while (vm.error.value == null) {
                kotlinx.coroutines.delay(10)
            }
        }

        assertNotNull(vm.error.value)
        assertTrue(vm.error.value!!.contains("Error", ignoreCase = true))
    }
}
