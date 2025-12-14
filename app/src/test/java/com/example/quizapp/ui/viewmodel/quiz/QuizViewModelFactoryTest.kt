package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import androidx.lifecycle.ViewModel
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class QuizViewModelFactoryTest {

    @Test
    fun create_devuelveQuizViewModel() {
        val context = mockk<Context>(relaxed = true)
        val factory = QuizViewModelFactory(context)

        val vm = factory.create(QuizViewModel::class.java)

        assertNotNull(vm)
        assertTrue(vm is QuizViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_claseDesconocida_lanzaExcepcion() {
        val context = mockk<Context>(relaxed = true)
        val factory = QuizViewModelFactory(context)

        factory.create(DummyViewModel::class.java)
    }

    private class DummyViewModel : ViewModel()
}