package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import androidx.lifecycle.ViewModel
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class FeedbackViewModelFactoryTest {

    @Test
    fun create_devuelveFeedbackViewModel() {
        val context = mockk<Context>(relaxed = true)
        val factory = FeedbackViewModelFactory(context)

        val vm = factory.create(FeedbackViewModel::class.java)

        assertNotNull(vm)
        assertTrue(vm is FeedbackViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_claseDesconocida_lanzaExcepcion() {
        val context = mockk<Context>(relaxed = true)
        val factory = FeedbackViewModelFactory(context)

        factory.create(DummyViewModel::class.java)
    }

    private class DummyViewModel : ViewModel()
}