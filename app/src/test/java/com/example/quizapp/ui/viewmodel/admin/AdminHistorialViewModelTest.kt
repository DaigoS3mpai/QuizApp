package com.example.quizapp.ui.viewmodel.admin

import android.content.Context
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class AdminHistorialViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AdminHistorialViewModel

    @Before
    fun setup() {
        val context = mockk<Context>(relaxed = true)
        viewModel = AdminHistorialViewModel(context)
    }

    @Test
    fun onUserIdChange_actualizaTextoYLimpiaError() = runTest {
        viewModel.onUserIdChange("123")

        val state = viewModel.uiState.value
        assertEquals("123", state.userIdText)
        assertNull(state.errorMsg)
    }

    @Test
    fun buscarHistorial_idVacio_muestraError() = runTest {
        viewModel.onUserIdChange("")
        viewModel.buscarHistorial()

        assertEquals(
            "Debes ingresar un ID de usuario.",
            viewModel.uiState.value.errorMsg
        )
    }

    @Test
    fun buscarHistorial_idNoNumerico_muestraError() = runTest {
        viewModel.onUserIdChange("abc")
        viewModel.buscarHistorial()

        assertEquals(
            "El ID debe ser un número válido.",
            viewModel.uiState.value.errorMsg
        )
    }
}
