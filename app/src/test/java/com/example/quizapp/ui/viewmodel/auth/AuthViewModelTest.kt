package com.example.quizapp.ui.viewmodel.auth

import android.content.Context
import com.example.quizapp.ui.viewmodel.Auth.AuthViewModel
import com.example.quizapp.ui.viewmodel.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        val context = mockk<Context>(relaxed = true)
        viewModel = AuthViewModel(context)
    }

    // ---------- LOGIN ----------

    @Test
    fun onLoginEmailChange_vacio_seteaError() {
        viewModel.onLoginEmailChange("")

        val state = viewModel.login.value
        assertNotNull(state.emailError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun onLoginPassChange_vacio_seteaError() {
        viewModel.onLoginPassChange("")

        val state = viewModel.login.value
        assertNotNull(state.passError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun login_noDebeEnviar_siCanSubmitFalse() = runTest {
        viewModel.submitLogin()
        assertFalse(viewModel.login.value.isSubmitting)
    }

    @Test
    fun clearLoginResult_limpiaSuccessYError() {
        viewModel.clearLoginResult()
        val state = viewModel.login.value

        assertFalse(state.success)
        assertNull(state.errorMsg)
    }

    // ---------- REGISTER ----------

    @Test
    fun onNameChange_invalido_seteaError() {
        viewModel.onNameChange("123")

        assertNotNull(viewModel.register.value.nameError)
    }

    @Test
    fun onRegisterEmailChange_invalido_seteaError() {
        viewModel.onRegisterEmailChange("correo-malo")

        assertNotNull(viewModel.register.value.emailError)
    }

    @Test
    fun submitRegister_noDebeEjecutar_siCanSubmitFalse() = runTest {
        viewModel.submitRegister()
        assertFalse(viewModel.register.value.isSubmitting)
    }

    @Test
    fun clearRegisterResult_limpiaEstado() {
        viewModel.clearRegisterResult()

        val state = viewModel.register.value
        assertFalse(state.success)
        assertNull(state.errorMsg)
    }

    // ---------- EDIT PROFILE ----------

    @Test
    fun initEditProfile_noLogueado_noHaceNada() {
        viewModel.initEditProfile()
        assertEquals("", viewModel.editProfile.value.name)
    }

    @Test
    fun onEditNameChange_invalido_seteaError() {
        viewModel.onEditNameChange("123")
        assertNotNull(viewModel.editProfile.value.nameError)
    }

    @Test
    fun submitEditProfile_noLogueado_noEjecuta() = runTest {
        viewModel.submitEditProfile()
        assertFalse(viewModel.editProfile.value.isSubmitting)
    }

    @Test
    fun clearEditProfileResult_limpiaEstado() {
        viewModel.clearEditProfileResult()
        assertFalse(viewModel.editProfile.value.success)
        assertNull(viewModel.editProfile.value.errorMsg)
    }

    // ---------- PASSWORD ----------

    @Test
    fun onPasswordEmailChange_vacio_seteaError() {
        viewModel.onPasswordEmailChange("")
        assertNotNull(viewModel.password.value.emailError)
    }

    @Test
    fun submitPasswordReset_noEjecuta_siCamposInvalidos() = runTest {
        viewModel.submitPasswordReset()
        assertFalse(viewModel.password.value.isSubmitting)
    }

    @Test
    fun clearPasswordResult_limpiaEstado() {
        viewModel.clearPasswordResult()
        assertFalse(viewModel.password.value.success)
        assertNull(viewModel.password.value.errorMsg)
    }
}
