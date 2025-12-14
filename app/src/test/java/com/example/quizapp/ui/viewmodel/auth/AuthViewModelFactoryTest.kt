package com.example.quizapp.ui.viewmodel.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModelFactory
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class AuthViewModelFactoryTest {

    @Test
    fun create_devuelveAuthViewModel() {
        val context = mockk<Context>(relaxed = true)
        val factory = AuthViewModelFactory(context)

        val vm = factory.create(AuthViewModel::class.java)

        assertNotNull(vm)
        assertTrue(vm is AuthViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_claseDesconocida_lanzaExcepcion() {
        val context = mockk<Context>(relaxed = true)
        val factory = AuthViewModelFactory(context)

        factory.create(DummyViewModel::class.java)
    }

    private class DummyViewModel : ViewModel()
}
