package com.example.quizapp.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ----------------- ESTADOS DE UI -----------------

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val profileImage: Bitmap? = null // Nuevo: imagen de perfil
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val profileImageUri: Uri? = null
)

// ----------------- MODELO TEMPORAL DE USUARIO -----------------

data class DemoUser(
    val name: String,
    val email: String,
    val pass: String,
    var profileImageUri: Uri? = null,
    var score: Int = 0
)

// ----------------- VIEWMODEL -----------------

class AuthViewModel : ViewModel() {

    companion object {
        private val USERS = mutableListOf(
            DemoUser(name = "Demo", email = "demo@duoc.cl", pass = "Demo123!", score = 50)
        )
    }

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- LOGIN -----------------

    private fun validateLoginUserOrEmail(input: String): String? {
        if (input.isBlank()) return "El usuario o correo es obligatorio"
        val isEmail = Patterns.EMAIL_ADDRESS.matcher(input).matches()
        val isUser = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ0-9]+$").matches(input)
        return if (!isEmail && !isUser) "Formato de usuario o correo inválido" else null
    }

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateLoginUserOrEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)
            val user = USERS.firstOrNull {
                it.email.equals(s.email, ignoreCase = true) || it.name.equals(s.email, ignoreCase = true)
            }
            val ok = user != null && user.pass == s.pass
            _login.update {
                it.copy(
                    isSubmitting = false,
                    success = ok,
                    errorMsg = if (!ok) "Credenciales inválidas" else null
                )
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO -----------------

    fun onNameChange(value: String) {
        _register.update { it.copy(name = value, nameError = validateNameLettersOnly(value)) }
        recomputeRegisterCanSubmit()
    }


    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)
            val duplicated = USERS.any { it.email.equals(s.email, ignoreCase = true) }
            if (duplicated) {
                _register.update { it.copy(isSubmitting = false, success = false, errorMsg = "El usuario ya existe") }
                return@launch
            }
            USERS.add(DemoUser(name = s.name.trim(), email = s.email.trim(), pass = s.pass, score = 0))
            _register.update { it.copy(isSubmitting = false, success = true, errorMsg = null) }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- FUNCIONES NUEVAS PARA PERFIL -----------------


    fun getCurrentUser(identifier: String): DemoUser? {
        return USERS.firstOrNull {
            it.email.equals(identifier, true) || it.name.equals(identifier, true)
        }
    }

    fun getGlobalScore(): Int {
        return USERS.sumOf { it.score }
    }

    // Función demo para incrementar puntaje
    fun addScoreToUser(identifier: String, points: Int) {
        val user = getCurrentUser(identifier)
        user?.score = user?.score?.plus(points) ?: 0
    }
    fun onProfileImageSelected(uri: Uri?) {
        _register.update { it.copy(profileImageUri = uri) }
        recomputeRegisterCanSubmit() // Opcional, si quieres que la imagen sea obligatoria
    }
}
