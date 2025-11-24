package com.example.quizapp.ui.viewmodel.Auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.storage.StorageHelper
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import com.example.quizapp.data.repository.AuthRepository
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
import java.io.File
import java.io.FileOutputStream

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

// 🔹 NUEVO: estado para editar perfil
data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val currentPass: String = "",
    val newPass: String = "",
    val confirmPass: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val currentPassError: String? = null,
    val newPassError: String? = null,
    val confirmPassError: String? = null,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class CurrentUserState(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val photo: Bitmap? = null,
    val loggedIn: Boolean = false,
    val puntaje: Int = 0,
    val puntajeGlobal: Int = 0,
    val idRol: Int = 1, // 1 = usuario, 2 = admin, 3 = quiz
    val isAdmin: Boolean = idRol == 2,
    val isQuiz: Boolean = idRol == 3
)

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


// ----------------- VIEWMODEL -----------------

class AuthViewModel(private val context: Context) : ViewModel() {

    private val repo = AuthRepository()

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // 🔹 NUEVO: estado para editar perfil
    private val _editProfile = MutableStateFlow(EditProfileUiState())
    val editProfile: StateFlow<EditProfileUiState> = _editProfile

    private val _password = MutableStateFlow(PasswordUiState())
    val password: StateFlow<PasswordUiState> = _password

    private val _currentUser = MutableStateFlow(CurrentUserState())
    val currentUser: StateFlow<CurrentUserState> = _currentUser

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val FOTO_URI_KEY = stringPreferencesKey("foto_perfil_uri")
    }

    // ----------------- LOGIN -----------------

    fun onLoginEmailChange(value: String) {
        _login.update {
            it.copy(
                email = value,
                emailError = if (value.isBlank())
                    "Ingresa tu correo o nombre de usuario"
                else null
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

            try {
                // Login por correo o nombre
                val usuario = repo.loginPorIdentificador(s.email.trim())

                if (usuario != null) {
                    // Cargamos posible foto guardada
                    val uriString = loadFotoUri()
                    val bmp = StorageHelper.loadImageFromInternalStorage(
                        context,
                        usuario.id.toInt()
                    )


                    withContext(Dispatchers.Main) {
                        setCurrentUser(usuario, bmp)
                        viewModelScope.launch { saveSession(usuario.id.toInt()) }
                        _login.update { it.copy(isSubmitting = false, success = true) }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _login.update {
                            it.copy(
                                isSubmitting = false,
                                success = false,
                                errorMsg = "Usuario no encontrado"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Error de red: ${e.message}"
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

    /**
     * Cuando el usuario selecciona una imagen (de galería o cámara)
     * la guardamos en disco y persistimos el URI en DataStore.
     */
    fun onProfileImageSelected(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Convertimos el URI a byte array
                val imageBytes = uriToByteArray(uri) ?: return@launch
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return@launch

                // Guardamos localmente usando StorageHelper
                val saved = StorageHelper.saveImageToInternalStorage(
                    context = context,
                    bitmap = bmp,
                    userId = _currentUser.value.id.toInt()
                )

                if (saved) {
                    // Guardamos la ruta del archivo en DataStore
                    val filePath = "${context.filesDir}/profile_images/user_${_currentUser.value.id}.png"
                    saveFotoUri(filePath)

                    // Actualizamos el estado del usuario actual
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

            try {
                Log.d("AuthViewModel", "🔵 submitRegister: iniciando registro...")
                delay(500)

                val current = _register.value

                // Verificar si ya existe usuario con ese correo en el microservicio
                val exists = repo.existeUsuarioPorCorreo(current.email.trim())
                if (exists) {
                    Log.d("AuthViewModel", "🟡 submitRegister: usuario ya existe")
                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            errorMsg = "El usuario ya existe"
                        )
                    }
                    return@launch
                }

                // Crear usuario remoto
                val creado = repo.registrarUsuario(
                    nombre = current.name.trim(),
                    correo = current.email.trim(),
                    clave = current.pass.trim(),
                    idRol = 1L,
                    idEstado = 1L
                )

                // Si en algún momento guardas foto con ese id, la podrías cargar así:
                val bmp = StorageHelper.loadImageFromInternalStorage(
                    context,
                    creado.id.toInt()
                )

                withContext(Dispatchers.Main) {
                    Log.d("AuthViewModel", "🟢 submitRegister: usuario creado con id=${creado.id}")

                    setCurrentUser(creado, bmp)
                    viewModelScope.launch { saveSession(creado.id.toInt()) }

                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            errorMsg = null
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "❌ submitRegister: excepción", e)
                _register.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = "Error de red: ${e.message}"
                    )
                }
            }
        }
    }


    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- PERFIL Y SESIÓN -----------------

    private fun setCurrentUser(user: UsuarioResponseDto, bitmap: Bitmap?) {
        val idRol = when (user.rol?.lowercase()) {
            "administrador" -> 2
            "quiz" -> 3
            else -> 1
        }

        _currentUser.update {
            CurrentUserState(
                id = user.id,
                name = user.nombre,
                email = user.correo,
                photo = bitmap,
                loggedIn = true,
                puntaje = user.puntaje,
                puntajeGlobal = user.puntajeGlobal,
                idRol = idRol
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

    // ----------------- 🔹 EDITAR PERFIL -----------------

    fun initEditProfile() {
        val user = _currentUser.value
        if (!user.loggedIn) return

        _editProfile.value = EditProfileUiState(
            name = user.name,
            email = user.email
        )
    }

    fun onEditNameChange(value: String) {
        _editProfile.update {
            it.copy(
                name = value,
                nameError = validateNameLettersOnly(value)
            )
        }
    }

    fun onEditEmailChange(value: String) {
        _editProfile.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
    }

    fun onEditCurrentPassChange(value: String) {
        _editProfile.update {
            it.copy(
                currentPass = value,
                currentPassError = if (value.isBlank()) "La contraseña actual es obligatoria" else null
            )
        }
    }

    fun onEditNewPassChange(value: String) {
        _editProfile.update { state ->
            val passError =
                if (value.isBlank()) null
                else validateStrongPassword(value)

            val confirmError =
                if (state.confirmPass.isBlank()) null
                else validateConfirm(value, state.confirmPass)

            state.copy(
                newPass = value,
                newPassError = passError,
                confirmPassError = confirmError
            )
        }
    }

    fun onEditConfirmPassChange(value: String) {
        _editProfile.update { state ->
            val confirmError =
                if (state.newPass.isBlank() && value.isBlank()) null
                else validateConfirm(state.newPass, value)

            state.copy(
                confirmPass = value,
                confirmPassError = confirmError
            )
        }
    }

    fun submitEditProfile() {
        val s = _editProfile.value
        val user = _currentUser.value
        if (!user.loggedIn || s.isSubmitting) return

        // Validaciones en front
        var hasError = false

        if (s.nameError != null || s.emailError != null) {
            hasError = true
        }
        if (s.currentPass.isBlank()) {
            _editProfile.update { it.copy(currentPassError = "La contraseña actual es obligatoria") }
            hasError = true
        }
        if (s.newPass.isNotBlank()) {
            val passErr = validateStrongPassword(s.newPass)
            val confirmErr = validateConfirm(s.newPass, s.confirmPass)

            _editProfile.update {
                it.copy(
                    newPassError = passErr,
                    confirmPassError = confirmErr
                )
            }

            if (passErr != null || confirmErr != null) hasError = true
        }

        if (hasError) return

        viewModelScope.launch(Dispatchers.IO) {
            _editProfile.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            try {
                val ok = repo.actualizarPerfil(
                    id = user.id,
                    nombre = s.name.trim(),
                    correo = s.email.trim(),
                    claveActual = s.currentPass.trim(),
                    claveNueva = if (s.newPass.isBlank()) null else s.newPass.trim()
                )

                withContext(Dispatchers.Main) {
                    if (!ok) {
                        _editProfile.update {
                            it.copy(
                                isSubmitting = false,
                                errorMsg = "No se pudo actualizar el perfil"
                            )
                        }
                        return@withContext
                    }

                    _currentUser.update {
                        it.copy(
                            name = s.name.trim(),
                            email = s.email.trim()
                        )
                    }

                    _editProfile.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            errorMsg = null
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _editProfile.update {
                        it.copy(
                            isSubmitting = false,
                            errorMsg = "Error al actualizar: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun clearEditProfileResult() {
        _editProfile.update { it.copy(success = false, errorMsg = null) }
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

    private fun saveBitmapToExternalStorage(bitmap: Bitmap): Uri? {
        return try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (dir != null && !dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "perfil_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ----------------- DATASTORE (SESION) -----------------

    private suspend fun saveSession(userId: Int) {
        context.sessionDataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    private suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }

    private suspend fun saveFotoUri(uri: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[FOTO_URI_KEY] = uri
        }
    }

    private suspend fun loadFotoUri(): String? {
        return context.sessionDataStore.data
            .map { it[FOTO_URI_KEY] }
            .first()
    }

    fun loadSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = context.sessionDataStore.data.map { it[USER_ID_KEY] ?: -1 }.first()
            if (userId != -1) {
                try {
                    val user = repo.obtenerUsuarioPorId(userId.toLong())

                    // Cargamos posible foto guardada
                    val uriString = loadFotoUri()
                    val bmp = StorageHelper.loadImageFromInternalStorage(
                        context,
                        userId
                    )

                    withContext(Dispatchers.Main) {
                        setCurrentUser(user, bmp)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // ----------------- 🔹 CAMBIAR CONTRASEÑA DESDE PASSWORD SCREEN -----------------

    fun onPasswordEmailChange(value: String) {
        _password.update {
            it.copy(
                email = value,
                emailError = if (value.isBlank()) "Ingresa tu correo" else null
            )
        }
    }

    fun onPasswordNewPassChange(value: String) {
        val error = validateStrongPassword(value)
        val confirmErr = if (_password.value.confirmPass.isNotEmpty())
            validateConfirm(value, _password.value.confirmPass)
        else null

        _password.update {
            it.copy(
                newPass = value,
                newPassError = error,
                confirmPassError = confirmErr
            )
        }
    }

    fun onPasswordConfirmChange(value: String) {
        val error = validateConfirm(_password.value.newPass, value)

        _password.update {
            it.copy(
                confirmPass = value,
                confirmPassError = error
            )
        }
    }

    fun submitPasswordReset() {
        val s = _password.value

        // Validaciones iniciales
        var hasError = false

        if (s.email.isBlank()) {
            _password.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        }

        if (s.newPass.isBlank()) {
            _password.update { it.copy(newPassError = "La contraseña es obligatoria") }
            hasError = true
        } else if (s.newPassError != null) {
            hasError = true
        }

        if (s.confirmPass.isBlank()) {
            _password.update { it.copy(confirmPassError = "Debes confirmar la contraseña") }
            hasError = true
        } else if (s.confirmPassError != null) {
            hasError = true
        }

        if (hasError || s.isSubmitting) return

        viewModelScope.launch(Dispatchers.IO) {
            _password.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            try {
                val ok = repo.actualizarPasswordPorCorreo(
                    identificador = s.email.trim(),
                    nuevaClave = s.newPass.trim()
                )

                withContext(Dispatchers.Main) {
                    if (ok) {
                        _password.update {
                            it.copy(
                                isSubmitting = false,
                                success = true
                            )
                        }
                    } else {
                        _password.update {
                            it.copy(
                                isSubmitting = false,
                                success = false,
                                errorMsg = "No se encontró un usuario con ese correo o nombre"
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _password.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Error al actualizar: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun clearPasswordResult() {
        _password.update { it.copy(success = false, errorMsg = null) }
    }
}
