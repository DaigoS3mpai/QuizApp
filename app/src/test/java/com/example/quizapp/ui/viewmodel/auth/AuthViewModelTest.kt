package com.example.quizapp.ui.viewmodel.auth

import android.content.Context
import com.example.quizapp.ui.viewmodel.Auth.*
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        // Context falso para el ViewModel
        mockContext = mockk(relaxed = true)
        viewModel = AuthViewModel(mockContext)
    }

    // ----------------------------------------------------
    // LOGIN
    // ----------------------------------------------------

    @Test
    fun `estado inicial de login esta vacio y no permite enviar`() {
        val state = viewModel.login.value

        assertEquals("", state.email)
        assertEquals("", state.pass)
        assertFalse(state.canSubmit)
        assertFalse(state.isSubmitting)
        assertNull(state.emailError)
        assertNull(state.passError)
        assertFalse(state.success)
        assertNull(state.errorMsg)
    }

    @Test
    fun `cuando email de login esta vacio marca error y no permite submit`() {
        viewModel.onLoginEmailChange("")

        val state = viewModel.login.value
        assertNotNull(state.emailError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun `cuando password de login esta vacia marca error y no permite submit`() {
        viewModel.onLoginPassChange("")

        val state = viewModel.login.value
        assertNotNull(state.passError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun `cuando email y password de login son validos se puede hacer submit`() {
        viewModel.onLoginEmailChange("user@example.com")
        viewModel.onLoginPassChange("ClaveSegura123!")

        val state = viewModel.login.value
        assertNull(state.emailError)
        assertNull(state.passError)
        assertTrue(state.canSubmit)
    }

    // ----------------------------------------------------
    // REGISTRO
    // ----------------------------------------------------

    @Test
    fun `estado inicial de registro esta vacio y no permite enviar`() {
        val state = viewModel.register.value

        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.pass)
        assertEquals("", state.confirm)
        assertFalse(state.canSubmit)
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.passError)
        assertNull(state.confirmError)
    }

    @Test
    fun `nombre con caracteres invalidos en registro marca error`() {
        // Tu validator permite letras y numeros, asi que usamos simbolos para forzar error
        viewModel.onNameChange("Camila@123")

        val state = viewModel.register.value
        assertNotNull(state.nameError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun `flujo completo de registro valido deja canSubmit en true`() {
        viewModel.onNameChange("Camila123")
        viewModel.onRegisterEmailChange("camila@example.com")
        viewModel.onRegisterPassChange("ClaveSegura123!")
        viewModel.onConfirmChange("ClaveSegura123!")

        val state = viewModel.register.value
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.passError)
        assertNull(state.confirmError)
        assertTrue(state.canSubmit)
    }

    // ----------------------------------------------------
    // EDITAR PERFIL (solo lógica de UI, sin repo)
    // ----------------------------------------------------

    /**
     * Usamos reflexión para forzar un CurrentUserState logueado,
     * porque la propiedad es privada en el ViewModel.
     */
    private fun givenLoggedUser(
        id: Long = 1L,
        name: String = "Ana",
        email: String = "ana@example.com"
    ) {
        val field = AuthViewModel::class.java.getDeclaredField("_currentUser")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(viewModel) as MutableStateFlow<CurrentUserState>
        flow.value = CurrentUserState(
            id = id,
            name = name,
            email = email,
            loggedIn = true
        )
    }

    @Test
    fun `initEditProfile copia nombre y correo del usuario actual`() {
        givenLoggedUser()

        viewModel.initEditProfile()
        val edit = viewModel.editProfile.value

        assertEquals("Ana", edit.name)
        assertEquals("ana@example.com", edit.email)
    }

    @Test
    fun `editar nombre y correo invalidos genera errores en editProfile`() {
        givenLoggedUser()
        viewModel.initEditProfile()

        // de nuevo, simbolos para que el nombre sea invalido segun tu regex
        viewModel.onEditNameChange("Ana!!")
        viewModel.onEditEmailChange("correo-invalido")

        val edit = viewModel.editProfile.value
        assertNotNull(edit.nameError)
        assertNotNull(edit.emailError)
    }

    @Test
    fun `editar nueva contrasena y confirmacion inconsistentes marcan error`() {
        givenLoggedUser()
        viewModel.initEditProfile()

        viewModel.onEditNewPassChange("ClaveSegura123!")
        viewModel.onEditConfirmPassChange("OtraClave123!")

        val edit = viewModel.editProfile.value
        // Al menos la confirmación debe tener error
        assertNotNull(edit.confirmPassError)
    }

    // ----------------------------------------------------
    // CAMBIO DE PASSWORD (PasswordUiState)
    // ----------------------------------------------------

    @Test
    fun `estado inicial de password esta vacio y sin errores`() {
        val state = viewModel.password.value

        assertEquals("", state.email)
        assertEquals("", state.newPass)
        assertEquals("", state.confirmPass)
        assertFalse(state.isSubmitting)
        assertFalse(state.success)
        assertNull(state.emailError)
        assertNull(state.newPassError)
        assertNull(state.confirmPassError)
    }

    @Test
    fun `email de password vacio marca error`() {
        viewModel.onPasswordEmailChange("")

        val state = viewModel.password.value
        assertNotNull(state.emailError)
    }

    @Test
    fun `nueva password debil genera error en passwordState`() {
        // Una clave muy corta es invalida segun validateStrongPassword
        viewModel.onPasswordNewPassChange("123")

        val state = viewModel.password.value
        assertNotNull(state.newPassError)
    }

    @Test
    fun `confirmar password distinta marca error de confirmacion`() {
        viewModel.onPasswordNewPassChange("ClaveSegura123!")
        viewModel.onPasswordConfirmChange("OtraClave123!")

        val state = viewModel.password.value
        assertNotNull(state.confirmPassError)
    }

    @Test
    fun `cuando nueva password y confirmacion coinciden no hay error de confirmacion`() {
        viewModel.onPasswordNewPassChange("ClaveSegura123!")
        viewModel.onPasswordConfirmChange("ClaveSegura123!")

        val state = viewModel.password.value
        assertNull(state.confirmPassError)
    }
}
