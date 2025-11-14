package com.example.quizapp.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.database.AppDatabase
import com.example.quizapp.data.user.UserDao
import com.example.quizapp.data.user.UserEntity
import com.example.quizapp.domain.validation.validateConfirm
import com.example.quizapp.domain.validation.validateEmail
import com.example.quizapp.domain.validation.validateNameLettersOnly
import com.example.quizapp.domain.validation.validateStrongPassword
import com.example.quizapp.utils.sessionDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

// ----------------- ESTADOS DE UI -----------------

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
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

data class CurrentUserState(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val photo: Bitmap? = null,
    val loggedIn: Boolean = false,
    val puntaje: Int = 0,
    val puntajeGlobal: Int = 0
)

// ----------------- VIEWMODEL -----------------

class AuthViewModel(private val context: Context) : ViewModel() {
    private val userDao: UserDao = AppDatabase.getInstance(context).usuarioDao()

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _currentUser = MutableStateFlow(CurrentUserState())
    val currentUser: StateFlow<CurrentUserState> = _currentUser

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }

    // ----------------- LOGIN -----------------

    fun onLoginEmailChange(value: String) {
        _login.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update {
            it.copy(
                pass = value,
                passError = if (value.isBlank()) "La contraseña es obligatoria" else null
            )
        }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        _login.update {
            it.copy(
                canSubmit = s.emailError == null &&
                        s.passError == null &&
                        s.email.isNotBlank() &&
                        s.pass.isNotBlank()
            )
        }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch(Dispatchers.IO) {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(400)

            val user = userDao.login(s.email.trim(), s.pass.trim())
            withContext(Dispatchers.Main) {
                if (user != null) {
                    _login.update { it.copy(isSubmitting = false, success = true) }
                    setCurrentUser(user)
                    viewModelScope.launch { saveSession(user.idUsuario) }
                } else {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Credenciales inválidas"
                        )
                    }
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO -----------------

    fun onNameChange(value: String) {
        _register.update {
            it.copy(
                name = value,
                nameError = validateNameLettersOnly(value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update {
            it.copy(
                pass = value,
                passError = validateStrongPassword(value)
            )
        }
        _register.update {
            it.copy(
                confirmError = validateConfirm(it.pass, it.confirm)
            )
        }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update {
            it.copy(
                confirm = value,
                confirmError = validateConfirm(it.pass, value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value

        val noErrors = listOf(
            s.nameError,
            s.emailError,
            s.passError,
            s.confirmError
        ).all { it == null }

        val filled = s.name.isNotBlank() && s.email.isNotBlank() &&
                s.pass.isNotBlank() && s.confirm.isNotBlank()

        _register.update {
            it.copy(canSubmit = noErrors && filled)
        }
    }

    fun onProfileImageSelected(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = _currentUser.value
                if (!user.loggedIn) return@launch

                val imageBytes = uriToByteArray(uri)
                if (imageBytes != null) {
                    userDao.updateProfilePhoto(user.id, imageBytes)
                    val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    _currentUser.update { it.copy(photo = bmp) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch(Dispatchers.IO) {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            delay(1500)

            val existingUser = userDao.findByEmailOrUsername(s.email.trim())
            if (existingUser != null) {
                _register.update {
                    it.copy(isSubmitting = false, errorMsg = "El usuario ya existe")
                }
                return@launch
            }

            val imageBytes = uriToByteArray(s.profileImageUri)

            val newUser = UserEntity(
                nombre = s.name.trim(),
                correo = s.email.trim(),
                clave = s.pass.trim(),
                fotoPerfil = imageBytes ?: ByteArray(0),
                idRol = 1,
                idEstado = 1,
                puntaje = 0,
                puntaje_global = 0
            )

            try {
                userDao.insert(newUser)
                val inserted = userDao.login(newUser.correo, newUser.clave)

                withContext(Dispatchers.Main) {
                    if (inserted != null) {
                        _register.update { it.copy(isSubmitting = false, success = true) }
                        setCurrentUser(inserted)
                        viewModelScope.launch { saveSession(inserted.idUsuario) }
                    } else {
                        _register.update { it.copy(isSubmitting = false, errorMsg = "Error al registrar usuario") }
                    }
                }
            } catch (e: Exception) {
                _register.update {
                    it.copy(isSubmitting = false, errorMsg = "Error de base de datos: ${e.message}")
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- PERFIL Y SESIÓN -----------------

    private fun setCurrentUser(user: UserEntity) {
        val bmp = if (user.fotoPerfil.isNotEmpty())
            BitmapFactory.decodeByteArray(user.fotoPerfil, 0, user.fotoPerfil.size)
        else null

        _currentUser.update {
            it.copy(
                id = user.idUsuario,
                name = user.nombre,
                email = user.correo,
                photo = bmp,
                loggedIn = true,
                puntaje = user.puntaje,
                puntajeGlobal = user.puntaje_global
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            clearSession()
            withContext(Dispatchers.Main) {
                _currentUser.value = CurrentUserState()
            }
        }
    }

    // ----------------- CONVERSIÓN IMAGEN -----------------

    private fun uriToByteArray(uri: Uri?): ByteArray? {
        if (uri == null) return null
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bmp = BitmapFactory.decodeStream(input)
                bmp?.let {
                    ByteArrayOutputStream().apply {
                        it.compress(Bitmap.CompressFormat.PNG, 100, this)
                    }.toByteArray()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // ----------------- DATASTORE -----------------

    private suspend fun saveSession(userId: Int) {
        context.sessionDataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    private suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }

    fun loadSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = context.sessionDataStore.data.map { it[USER_ID_KEY] ?: -1 }.first()
            if (userId != -1) {
                val user = userDao.getById(userId)
                if (user != null) withContext(Dispatchers.Main) { setCurrentUser(user) }
            }
        }
    }

    // ----------------- OLVIDAR CONTRASEÑA -----------------

    data class PasswordUiState(
        val email: String = "",
        val newPass: String = "",
        val confirmPass: String = "",
        val emailError: String? = null,
        val newPassError: String? = null,
        val confirmPassError: String? = null,
        val isSubmitting: Boolean = false,
        val success: Boolean = false,
        val errorMsg: String? = null
    )

    private val _password = MutableStateFlow(PasswordUiState())
    val password: StateFlow<PasswordUiState> = _password

    fun onPasswordEmailChange(value: String) {
        _password.update {
            it.copy(
                email = value,
                emailError = validateEmail(value),
                errorMsg = null
            )
        }
    }

    fun onPasswordNewPassChange(value: String) {
        _password.update {
            it.copy(
                newPass = value,
                newPassError = validateStrongPassword(value),
                confirmPassError = validateConfirm(value, it.confirmPass),
                errorMsg = null
            )
        }
    }

    fun onPasswordConfirmChange(value: String) {
        _password.update {
            it.copy(
                confirmPass = value,
                confirmPassError = validateConfirm(it.newPass, value),
                errorMsg = null
            )
        }
    }

    fun submitPasswordReset() {
        val s = _password.value

        if (s.isSubmitting) return

        if (s.emailError != null || s.email.isBlank()) {
            _password.update { it.copy(errorMsg = "Ingresa un correo válido") }
            return
        }

        if (s.newPassError != null || s.confirmPassError != null) {
            _password.update { it.copy(errorMsg = "Corrige los errores antes de continuar") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _password.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            try {
                val user = userDao.findByEmailOrUsername(s.email.trim())

                if (user == null) {
                    _password.update {
                        it.copy(isSubmitting = false, errorMsg = "Usuario no encontrado")
                    }
                    return@launch
                }

                val updated = userDao.updatePasswordByIdentifier(s.email.trim(), s.newPass.trim())

                withContext(Dispatchers.Main) {
                    if (updated > 0) {
                        _password.update { it.copy(isSubmitting = false, success = true) }
                    } else {
                        _password.update {
                            it.copy(isSubmitting = false, errorMsg = "Error al actualizar contraseña")
                        }
                    }
                }
            } catch (e: Exception) {
                _password.update { it.copy(isSubmitting = false, errorMsg = e.message) }
            }
        }
    }

    fun clearPasswordResult() {
        _password.update { PasswordUiState() }
    }
}
