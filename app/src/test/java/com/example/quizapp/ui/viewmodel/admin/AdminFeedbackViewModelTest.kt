package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.quizapp.data.remote.dto.FeedbackResponseDto
import com.example.quizapp.data.repository.FeedbackRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test

class AdminFeedbackViewModelTest {

    private val repo = mockk<FeedbackRepository>(relaxed = true)

    @Test
    fun init_cargaPendientes_yActualizaEstado() = runBlocking {
        val item = mockk<FeedbackResponseDto>(relaxed = true)
        coEvery { repo.listarPendientes() } returns listOf(item)

        val vm = AdminFeedbackViewModel(repo)

        val state = withTimeout(3000) {
            vm.uiState.first { it.isLoading == false && it.items.isNotEmpty() }
        }

        assertEquals(1, state.items.size)
        assertNull(state.errorMsg)

        coVerify(exactly = 1) { repo.listarPendientes() }
    }

    @Test
    fun cargarFeedbackPendientes_listaVacia_muestraMensajeNoHayPendiente() = runBlocking {
        coEvery { repo.listarPendientes() } returns emptyList()

        val vm = AdminFeedbackViewModel(repo)

        val state = withTimeout(3000) {
            vm.uiState.first { it.isLoading == false && it.errorMsg != null }
        }

        assertEquals("No hay feedback pendiente.", state.errorMsg)
        assertTrue(state.items.isEmpty())
    }

    @Test
    fun cambiarFiltro_aTodos_llamaRepoTodos_yActualizaFiltro() = runBlocking {
        val item = mockk<FeedbackResponseDto>(relaxed = true)
        coEvery { repo.listarPendientes() } returns emptyList() // init
        coEvery { repo.listarTodos() } returns listOf(item)

        val vm = AdminFeedbackViewModel(repo)

        vm.cambiarFiltro(FeedbackFiltro.TODOS)

        val state = withTimeout(3000) {
            vm.uiState.first { it.filtro == FeedbackFiltro.TODOS && it.isLoading == false }
        }

        assertEquals(FeedbackFiltro.TODOS, state.filtro)
        assertEquals(1, state.items.size)

        coVerify(exactly = 1) { repo.listarTodos() }
    }

    @Test
    fun cargarFeedbackTodos_listaVacia_muestraMensajeNoHayRegistrado() = runBlocking {
        coEvery { repo.listarPendientes() } returns emptyList() // init
        coEvery { repo.listarTodos() } returns emptyList()

        val vm = AdminFeedbackViewModel(repo)

        vm.cambiarFiltro(FeedbackFiltro.TODOS)

        val state = withTimeout(3000) {
            vm.uiState.first {
                it.filtro == FeedbackFiltro.TODOS &&
                        it.isLoading == false &&
                        it.errorMsg != null
            }
        }

        assertEquals("No hay feedback registrado.", state.errorMsg)
        assertTrue(state.items.isEmpty())
    }

    @Test
    fun resolverFeedback_enPendientes_llamaResolver_yRecargaPendientes() = runBlocking {
        coEvery { repo.listarPendientes() } returns emptyList()
        coEvery { repo.resolver(10L) } returns mockk(relaxed = true)

        val vm = AdminFeedbackViewModel(repo)

        vm.resolverFeedback(10L)

        withTimeout(3000) { vm.uiState.first { it.isLoading == false } }

        coVerifyOrder {
            repo.resolver(10L)
            repo.listarPendientes()
        }
    }

    @Test
    fun resolverFeedback_error_seteaErrorMsg() = runBlocking {
        coEvery { repo.listarPendientes() } returns emptyList()
        coEvery { repo.resolver(1L) } throws RuntimeException("fail")
        val vm = AdminFeedbackViewModel(repo)

        vm.resolverFeedback(1L)

        withTimeout(3000) {
            while (vm.uiState.value.errorMsg?.contains("Error al resolver feedback") != true) {
                kotlinx.coroutines.delay(10)
            }
        }

        assertTrue(vm.uiState.value.errorMsg!!.contains("Error al resolver feedback"))
    }


    @Test
    fun factory_create_devuelveAdminFeedbackViewModel() {
        val context = mockk<Context>(relaxed = true)
        val factory = AdminFeedbackViewModelFactory(context)

        val vm = factory.create(AdminFeedbackViewModel::class.java)

        assertNotNull(vm)
        assertTrue(vm is AdminFeedbackViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun factory_create_conClaseDesconocida_lanzaExcepcion() {
        val context = mockk<Context>(relaxed = true)
        val factory = AdminFeedbackViewModelFactory(context)
        factory.create(DummyViewModel::class.java)
    }

    private class DummyViewModel : ViewModel()
}
