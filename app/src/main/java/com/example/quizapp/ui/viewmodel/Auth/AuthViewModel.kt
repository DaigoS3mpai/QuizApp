package com.example.quizapp.ui.viewmodel.Auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.storage.StorageHelper
import com.example.quizapp.data.remote.dto.SecurityQuestionDto
import com.example.quizapp.data.remote.dto.SetupSecurityQuestionsRequestDto
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import com.example.quizapp.data.remote.dto.VerifyRecoveryRequestDto
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// =====================================================
// DATA CLASSES (UI STATES)
// =====================================================

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
    val profileImageUri: Uri? = null,
    val createdUserId: Long? = null
)

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
    val idRol: Int = 1,
    val isAdmin: Boolean = idRol == 2,
    val isQuiz: Boolean = idRol == 3
)

data class PasswordUiState(
    val email: String = "",
    val token: String = "",
    val newPass: String = "",
    val confirmPass: String = "",
    val emailError: String? = null,
    val tokenError: String? = null,
    val newPassError: String? = null,
    val confirmPassError: String? = null,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RecoveryState(
    val isLoading: Boolean = false,
    val recoveryUserId: Long? = null,
    val questions: List<com.example.quizapp.data.remote.dto.RecoveryQuestionDto> = emptyList(),
    val token: String? = null,
    val errorMsg: String? = null
)

data class SetupSecurityQuestionsUiState(
    val isLoading: Boolean = false,
    val questions: List<SecurityQuestionDto> = emptyList(),
    val errorMsg: String? = null,
    val success: Boolean = false
)

// =====================================================
// VIEWMODEL
// =====================================================

class AuthViewModel(private val context: Context) : ViewModel() {

    // ----------------- REPOSITORY -----------------
    private val repo = AuthRepository()

    // =====================================================
    // STATEFLOW (EXPOSED STATES)
    // =====================================================

    // ----------------- LOGIN STATEFLOW -----------------
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    // ----------------- REGISTER STATEFLOW -----------------
    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- EDIT PROFILE STATEFLOW -----------------
    private val _editProfile = MutableStateFlow(EditProfileUiState())
    val editProfile: StateFlow<EditProfileUiState> = _editProfile

    // ----------------- PASSWORD STATEFLOW -----------------
    private val _password = MutableStateFlow(PasswordUiState())
    val password: StateFlow<PasswordUiState> = _password

    // ----------------- CURRENT USER STATEFLOW -----------------
    private val _currentUser = MutableStateFlow(CurrentUserState())
    val currentUser: StateFlow<CurrentUserState> = _currentUser

    // ----------------- RECOVERY STATEFLOW -----------------
    private val _recovery = MutableStateFlow(RecoveryState())
    val recovery = _recovery.asStateFlow()

    // ----------------- SETUP SECURITY QUESTIONS STATEFLOW -----------------
    private val _setupSQ = MutableStateFlow(SetupSecurityQuestionsUiState())
    val setupSQ = _setupSQ.asStateFlow()

    // ----------------- DATASTORE KEYS -----------------
    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val FOTO_URI_KEY = stringPreferencesKey("foto_perfil_uri")
    }

    // =====================================================
    // ----------------- FUN LOGIN -----------------
    // =====================================================

    fun onLoginEmailChange(value: String) {
        _login.update {
            it.copy(
                email = value,
                emailError = if (value.isBlank()) "Ingresa tu correo o nombre de usuario" else null
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
                val usuario = repo.login(
                    identificador = s.email.trim(),
                    clave = s.pass.trim()
                )

                if (usuario != null) {
                    val bmp = StorageHelper.loadImageFromInternalStorage(context, usuario.id.toInt())

                    withContext(Dispatchers.Main) {
                        setCurrentUser(usuario, bmp)
                        saveSession(usuario.id.toInt())
                        _login.update { it.copy(isSubmitting = false, success = true) }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _login.update { it.copy(isSubmitting = false, success = false, errorMsg = "Credenciales inválidas") }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _login.update { it.copy(isSubmitting = false, success = false, errorMsg = "Error de red: ${e.message}") }
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // =====================================================
    // ----------------- FUN REGISTER -----------------
    // =====================================================

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

        viewModelScope.launch(Dispatchers.IO) {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false, createdUserId = null) }

            try {
                delay(300)
                val current = _register.value

                val exists = repo.existeUsuarioPorCorreo(current.email.trim())
                if (exists) {
                    withContext(Dispatchers.Main) {
                        _register.update { it.copy(isSubmitting = false, errorMsg = "El usuario ya existe") }
                    }
                    return@launch
                }

                val creado = repo.registrarUsuario(
                    nombre = current.name.trim(),
                    correo = current.email.trim(),
                    clave = current.pass.trim(),
                    idRol = 1L,
                    idEstado = 1L
                )

                val bmp = StorageHelper.loadImageFromInternalStorage(context, creado.id.toInt())

                withContext(Dispatchers.Main) {
                    setCurrentUser(creado, bmp)
                    saveSession(creado.id.toInt())

                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            errorMsg = null,
                            createdUserId = creado.id
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _register.update { it.copy(isSubmitting = false, success = false, errorMsg = "Error de red: ${e.message}") }
                }
            }
        }
    }

    // Útil para el flujo: se navega con createdUserId y luego se limpia, así no se repite al recomponer
    fun consumeRegisterCreatedUserId(): Long? {
        val id = _register.value.createdUserId
        if (id != null) {
            _register.update { it.copy(createdUserId = null) }
        }
        return id
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null, createdUserId = null) }
    }

    // =====================================================
    // ----------------- FUN CURRENT USER / SESSION -----------------
    // =====================================================

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
            _currentUser.value = CurrentUserState()
        }
    }

    fun loadSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = context.sessionDataStore.data.map { it[USER_ID_KEY] ?: -1 }.first()
            if (userId == -1) return@launch

            runCatching {
                val user = repo.obtenerUsuarioPorId(userId.toLong())

                val bmpFromDb: Bitmap? = runCatching {
                    user.fotoPerfilBase64?.let { base64 ->
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }.getOrNull()

                val bmpFromCache = StorageHelper.loadImageFromInternalStorage(context, userId)
                val finalBitmap = bmpFromDb ?: bmpFromCache

                if (bmpFromDb != null) {
                    StorageHelper.saveImageToInternalStorage(context, bmpFromDb, userId)
                    val filePath = "${context.filesDir}/profile_images/user_${userId}.png"
                    saveFotoUri(filePath)
                }

                withContext(Dispatchers.Main) {
                    setCurrentUser(user, finalBitmap)
                }
            }
        }
    }

    // =====================================================
    // ----------------- FUN EDIT PROFILE -----------------
    // =====================================================

    fun initEditProfile() {
        val user = _currentUser.value
        if (!user.loggedIn) return
        _editProfile.value = EditProfileUiState(name = user.name, email = user.email)
    }

    fun onEditNameChange(value: String) {
        _editProfile.update { it.copy(name = value, nameError = validateNameLettersOnly(value)) }
    }

    fun onEditEmailChange(value: String) {
        _editProfile.update { it.copy(email = value, emailError = validateEmail(value)) }
    }

    fun onEditCurrentPassChange(value: String) {
        _editProfile.update { it.copy(currentPass = value, currentPassError = if (value.isBlank()) "La contraseña actual es obligatoria" else null) }
    }

    fun onEditNewPassChange(value: String) {
        _editProfile.update { state ->
            val passError = if (value.isBlank()) null else validateStrongPassword(value)
            val confirmError = if (state.confirmPass.isBlank()) null else validateConfirm(value, state.confirmPass)
            state.copy(newPass = value, newPassError = passError, confirmPassError = confirmError)
        }
    }

    fun onEditConfirmPassChange(value: String) {
        _editProfile.update { state ->
            val confirmError = if (state.newPass.isBlank() && value.isBlank()) null else validateConfirm(state.newPass, value)
            state.copy(confirmPass = value, confirmPassError = confirmError)
        }
    }

    fun submitEditProfile() {
        val s = _editProfile.value
        val user = _currentUser.value
        if (!user.loggedIn || s.isSubmitting) return

        var hasError = false
        if (s.nameError != null || s.emailError != null) hasError = true

        if (s.currentPass.isBlank()) {
            _editProfile.update { it.copy(currentPassError = "La contraseña actual es obligatoria") }
            hasError = true
        }

        if (s.newPass.isNotBlank()) {
            val passErr = validateStrongPassword(s.newPass)
            val confirmErr = validateConfirm(s.newPass, s.confirmPass)
            _editProfile.update { it.copy(newPassError = passErr, confirmPassError = confirmErr) }
            if (passErr != null || confirmErr != null) hasError = true
        }

        if (hasError) return

        viewModelScope.launch(Dispatchers.IO) {
            _editProfile.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            runCatching {
                repo.actualizarPerfil(
                    id = user.id,
                    nombre = s.name.trim(),
                    correo = s.email.trim(),
                    claveActual = s.currentPass.trim(),
                    claveNueva = if (s.newPass.isBlank()) null else s.newPass.trim()
                )
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    _currentUser.update { it.copy(name = s.name.trim(), email = s.email.trim()) }
                    _editProfile.update { it.copy(isSubmitting = false, success = true, errorMsg = null) }
                }
            }.onFailure { e ->
                withContext(Dispatchers.Main) {
                    _editProfile.update { it.copy(isSubmitting = false, errorMsg = "Error al actualizar: ${e.message}") }
                }
            }
        }
    }

    fun clearEditProfileResult() {
        _editProfile.update { it.copy(success = false, errorMsg = null) }
    }

    // =====================================================
    // ----------------- FUN PASSWORD -----------------
    // =====================================================

    fun onPasswordEmailChange(value: String) {
        _password.update { it.copy(email = value, emailError = if (value.isBlank()) "Ingresa tu correo" else null) }
    }

    fun onPasswordTokenChange(value: String) {
        _password.update { it.copy(token = value, tokenError = if (value.isBlank()) "Token inválido" else null) }
    }

    fun onPasswordNewPassChange(value: String) {
        val error = validateStrongPassword(value)
        val confirmErr = if (_password.value.confirmPass.isNotEmpty()) validateConfirm(value, _password.value.confirmPass) else null
        _password.update { it.copy(newPass = value, newPassError = error, confirmPassError = confirmErr) }
    }

    fun onPasswordConfirmChange(value: String) {
        val error = validateConfirm(_password.value.newPass, value)
        _password.update { it.copy(confirmPass = value, confirmPassError = error) }
    }

    fun submitPasswordReset() {
        val s = _password.value
        var hasError = false

        if (s.email.isBlank()) {
            _password.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        }
        if (s.newPass.isBlank()) {
            _password.update { it.copy(newPassError = "La contraseña es obligatoria") }
            hasError = true
        } else if (s.newPassError != null) hasError = true

        if (s.confirmPass.isBlank()) {
            _password.update { it.copy(confirmPassError = "Debes confirmar la contraseña") }
            hasError = true
        } else if (s.confirmPassError != null) hasError = true

        if (hasError || s.isSubmitting) return

        viewModelScope.launch(Dispatchers.IO) {
            _password.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            runCatching {
                repo.actualizarPasswordPorCorreo(
                    identificador = s.email.trim(),
                    nuevaClave = s.newPass.trim()
                )
            }.onSuccess { ok ->
                withContext(Dispatchers.Main) {
                    if (ok) _password.update { it.copy(isSubmitting = false, success = true) }
                    else _password.update { it.copy(isSubmitting = false, success = false, errorMsg = "No se encontró un usuario con ese correo o nombre") }
                }
            }.onFailure { e ->
                withContext(Dispatchers.Main) {
                    _password.update { it.copy(isSubmitting = false, success = false, errorMsg = "Error al actualizar: ${e.message}") }
                }
            }
        }
    }

    fun submitPasswordRecoveryReset() {
        val s = _password.value
        var hasError = false

        if (s.token.isBlank()) {
            _password.update { it.copy(tokenError = "Token inválido") }
            hasError = true
        }
        if (s.newPass.isBlank()) {
            _password.update { it.copy(newPassError = "La contraseña es obligatoria") }
            hasError = true
        } else if (s.newPassError != null) hasError = true

        if (s.confirmPass.isBlank()) {
            _password.update { it.copy(confirmPassError = "Debes confirmar la contraseña") }
            hasError = true
        } else if (s.confirmPassError != null) hasError = true

        if (hasError || s.isSubmitting) return

        viewModelScope.launch(Dispatchers.IO) {
            _password.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            repo.resetPasswordRecovery(
                token = s.token.trim(),
                newPassword = s.newPass.trim()
            ).onSuccess {
                withContext(Dispatchers.Main) { _password.update { it.copy(isSubmitting = false, success = true) } }
            }.onFailure { e ->
                withContext(Dispatchers.Main) { _password.update { it.copy(isSubmitting = false, success = false, errorMsg = e.message ?: "Error al actualizar") } }
            }
        }
    }

    fun clearPasswordResult() {
        _password.update { it.copy(success = false, errorMsg = null) }
    }

    // =====================================================
    // ----------------- FUN FORGOT PASSWORD (RECOVERY) -----------------
    // =====================================================

    fun loadRecoveryQuestions(identificador: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _recovery.update { it.copy(isLoading = true, errorMsg = null, token = null) }

            val result = repo.getRecoveryQuestions(identificador.trim())

            withContext(Dispatchers.Main) {
                result.onSuccess { resp ->
                    _recovery.update { it.copy(isLoading = false, recoveryUserId = resp.userId, questions = resp.questions, errorMsg = null) }
                }.onFailure { e ->
                    _recovery.update { it.copy(isLoading = false, errorMsg = e.message ?: "No se pudieron cargar las preguntas") }
                }
            }
        }
    }

    fun verifyRecoveryAnswers(
        items: List<Pair<Int, String>>,
        onOk: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val userId = _recovery.value.recoveryUserId
        if (userId == null) {
            onFail("No se pudo obtener el usuario")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _recovery.update { it.copy(isLoading = true, errorMsg = null, token = null) }

            val dtoItems = items.map { (pid, ans) ->
                VerifyRecoveryRequestDto.Item(preguntaId = pid, respuesta = ans.trim())
            }

            val result = repo.verifyRecovery(userId, dtoItems)

            withContext(Dispatchers.Main) {
                result.onSuccess { resp ->
                    _recovery.update { it.copy(isLoading = false, token = resp.token, errorMsg = null) }
                    onOk(resp.token)
                }.onFailure { e ->
                    val msg = e.message ?: "Respuestas incorrectas"
                    _recovery.update { it.copy(isLoading = false, errorMsg = msg) }
                    onFail(msg)
                }
            }
        }
    }

    fun clearRecoveryError() {
        _recovery.update { it.copy(errorMsg = null) }
    }

    // =====================================================
    // ----------------- FUN SETUP SECURITY QUESTIONS -----------------
    // =====================================================

    fun loadSecurityQuestionsForSetup() {
        viewModelScope.launch(Dispatchers.IO) {
            _setupSQ.update { it.copy(isLoading = true, errorMsg = null, success = false) }

            val result = repo.getSecurityQuestions()

            withContext(Dispatchers.Main) {
                result.onSuccess { list ->
                    _setupSQ.update { it.copy(isLoading = false, questions = list, errorMsg = null) }
                }.onFailure { e ->
                    _setupSQ.update { it.copy(isLoading = false, errorMsg = e.message ?: "No se pudieron cargar") }
                }
            }
        }
    }

    fun submitSecurityQuestionsSetup(
        userId: Long,
        q1: Int, a1: String,
        q2: Int, a2: String,
        q3: Int, a3: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _setupSQ.update { it.copy(isLoading = true, errorMsg = null, success = false) }

            val items = listOf(
                SetupSecurityQuestionsRequestDto.Item(q1, a1.trim()),
                SetupSecurityQuestionsRequestDto.Item(q2, a2.trim()),
                SetupSecurityQuestionsRequestDto.Item(q3, a3.trim())
            )

            val result = repo.setupSecurityQuestions(userId, items)

            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _setupSQ.update { it.copy(isLoading = false, success = true, errorMsg = null) }
                }.onFailure { e ->
                    _setupSQ.update { it.copy(isLoading = false, success = false, errorMsg = e.message ?: "No se pudo guardar") }
                }
            }
        }
    }

    fun clearSetupSecurityQuestionsResult() {
        _setupSQ.update { it.copy(success = false, errorMsg = null) }
    }

    // =====================================================
    // ----------------- FUN PROFILE IMAGE -----------------
    // =====================================================

    fun onProfileImageSelected(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUserId = _currentUser.value.id
                if (currentUserId == 0L || uri == null) return@launch

                val originalBmp = context.contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input)
                } ?: return@launch

                val scaledBmp = scaleBitmapToMaxSize(originalBmp, maxSize = 720)
                val imageBytes = bitmapToJpegBytes(scaledBmp, quality = 80)

                val saved = StorageHelper.saveImageToInternalStorage(context, scaledBmp, currentUserId.toInt())
                if (saved) {
                    val filePath = "${context.filesDir}/profile_images/user_${currentUserId}.png"
                    saveFotoUri(filePath)
                    _currentUser.update { it.copy(photo = scaledBmp) }
                }

                runCatching { repo.actualizarFotoPerfilUsandoPut(currentUserId, imageBytes) }
                    .onFailure { e -> Log.e("AuthViewModel", "Error subiendo foto de perfil: ${e.message}") }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error procesando imagen: ${e.message}")
            }
        }
    }

    private fun scaleBitmapToMaxSize(bitmap: Bitmap, maxSize: Int = 720): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToJpegBytes(bitmap: Bitmap, quality: Int = 80): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        return baos.toByteArray()
    }

    private fun saveBitmapToExternalStorage(bitmap: Bitmap): Uri? {
        return try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (dir != null && !dir.exists()) dir.mkdirs()
            val file = File(dir, "perfil_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
            Uri.fromFile(file)
        } catch (_: Exception) {
            null
        }
    }

    // =====================================================
    // ----------------- FUN DATASTORE (SESSION) -----------------
    // =====================================================

    private suspend fun saveSession(userId: Int) {
        context.sessionDataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    private suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }

    private suspend fun saveFotoUri(uri: String) {
        context.sessionDataStore.edit { prefs -> prefs[FOTO_URI_KEY] = uri }
    }
}
