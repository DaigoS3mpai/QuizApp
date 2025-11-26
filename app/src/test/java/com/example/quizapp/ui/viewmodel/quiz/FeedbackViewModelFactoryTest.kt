package com.example.quizapp.ui.viewmodel.quiz

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackViewModelFactoryTest {

    @Test
    fun `create devuelve una instancia de FeedbackViewModel`() {
        val context: Context = mockk(relaxed = true)
        val factory = FeedbackViewModelFactory(context)

        val vm = factory.create(FeedbackViewModel::class.java)

        assertTrue(vm is FeedbackViewModel)
    }
}
