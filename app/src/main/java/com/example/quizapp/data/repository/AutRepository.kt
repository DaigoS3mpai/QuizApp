package com.example.quizapp.data.repository

import android.util.Base64
import com.example.quizapp.data.remote.ApiClient
import com.example.quizapp.data.remote.dto.LoginRequestDto
import com.example.quizapp.data.remote.dto.ResetPasswordRequestDto
import com.example.quizapp.data.remote.dto.SecurityQuestionDto
import com.example.quizapp.data.remote.dto.SetupSecurityQuestionsRequestDto
import com.example.quizapp.data.remote.dto.UsuarioRequestDto
import com.example.quizapp.data.remote.dto.UsuarioResponseDto
import com.example.quizapp.data.remote.dto.VerifyRecoveryRequestDto

class AuthRepository {

    private val api = ApiClient.authApi

    // Convierte nombre de rol a id numérico
    private fun rolNombreToId(rol: String?): Long =
        when (rol?.lowercase()) {
            "administrador" -> 2L
            "quiz" -> 3L
            else -> 1L // usuario normal
        }

    // Por ahora dejamos fijo 1L como "Activo"
    private fun estadoNombreToId(estado: String?): Long =
        1L

    // Login por correo o nombre (ya lo usas en el ViewModel)
    suspend fun loginPorIdentificador(identificador: String): UsuarioResponseDto? {
        val usuarios = api.getUsuarios()
        val idLower = identificador.trim().lowercase()

        return usuarios.find {
            it.correo.lowercase() == idLower ||
                    it.nombre.lowercase() == idLower
        }
    }

    // Registrar usuario nuevo
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        clave: String,
        idRol: Long = 1L,
        idEstado: Long = 1L
    ): UsuarioResponseDto {
        val body = UsuarioRequestDto(
            nombre = nombre,
            correo = correo,
            clave = clave,
            idRol = idRol,
            idEstado = idEstado,
            puntaje = 0,
            puntajeGlobal = 0
        )
        return api.crearUsuario(body)
    }

    // Verifica si ya existe un usuario por correo
    suspend fun existeUsuarioPorCorreo(correo: String): Boolean {
        val usuarios = api.getUsuarios()
        return usuarios.any { it.correo.equals(correo.trim(), ignoreCase = true) }
    }

    // Obtener por id (para sesión)
    suspend fun obtenerUsuarioPorId(id: Long): UsuarioResponseDto =
        api.getUsuarioPorId(id)

    // NUEVO: Actualizar contraseña buscando por correo o nombre
    suspend fun actualizarPasswordPorCorreo(
        identificador: String,
        nuevaClave: String
    ): Boolean {
        val usuarios = api.getUsuarios()
        val idLower = identificador.trim().lowercase()

        val usuario = usuarios.find {
            it.correo.lowercase() == idLower ||
                    it.nombre.lowercase() == idLower
        } ?: return false // no se encontró

        val idRol = rolNombreToId(usuario.rol)
        val idEstado = estadoNombreToId(usuario.estado)

        // Armamos el body completo para el PUT
        val body = UsuarioRequestDto(
            nombre = usuario.nombre,
            correo = usuario.correo,
            clave = nuevaClave,
            idRol = idRol,
            idEstado = idEstado,
            puntaje = usuario.puntaje,
            puntajeGlobal = usuario.puntajeGlobal
        )

        api.actualizarUsuario(usuario.id, body)
        return true
    }
    // Actualizar nombre, correo y contraseña (opcional)
    suspend fun actualizarPerfil(
        id: Long,
        nombre: String,
        correo: String,
        claveActual: String,
        claveNueva: String?
    ): Boolean {

        // 1) Traemos el usuario actual para conservar rol/estado/puntajes
        val usuarioActual = api.getUsuarioPorId(id)

        val idRol = rolNombreToId(usuarioActual.rol)
        val idEstado = estadoNombreToId(usuarioActual.estado)

        // 2) Si no hay nueva contraseña, usamos la "actual" que escribió el usuario
        val claveFinal = claveNueva ?: claveActual

        // 3) Armamos el body para el PUT
        val body = UsuarioRequestDto(
            nombre = nombre,
            correo = correo,
            clave = claveFinal,
            idRol = idRol,
            idEstado = idEstado,
            puntaje = usuarioActual.puntaje,
            puntajeGlobal = usuarioActual.puntajeGlobal
        )

        // 4) Hacemos el PUT al microservicio
        api.actualizarUsuario(id, body)
        return true
    }
    // Listar todos los usuarios (para admin)
    suspend fun obtenerTodosLosUsuarios(): List<UsuarioResponseDto> =
        api.getUsuarios()

    // Eliminar usuario
    suspend fun eliminarUsuario(id: Long) {
        val response = api.eliminarUsuario(id)

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}")
        }
    }

    // Actualizar solo puntajes desde admin
    suspend fun actualizarPuntajesAdmin(
        id: Long,
        nuevoPuntaje: Int,
        nuevoPuntajeGlobal: Int
    ): UsuarioResponseDto {

        val usuarioActual = api.getUsuarioPorId(id)
        val idRol = rolNombreToId(usuarioActual.rol)
        val idEstado = estadoNombreToId(usuarioActual.estado)

        val body = UsuarioRequestDto(
            nombre = usuarioActual.nombre,
            correo = usuarioActual.correo,
            // TODO: aquí el backend idealmente debería ignorar la clave si no cambia.
            // Usamos una cadena vacía o un valor "dummy" según cómo esté hecho tu microservicio.
            clave = "",
            idRol = idRol,
            idEstado = idEstado,
            puntaje = nuevoPuntaje,
            puntajeGlobal = nuevoPuntajeGlobal
        )

        return api.actualizarUsuario(id, body)
    }

    suspend fun actualizarFotoPerfilUsandoPut(
        id: Long,
        bytesImagen: ByteArray?
    ): UsuarioResponseDto {
        // 1) Traemos el usuario actual desde el backend
        val usuarioActual = api.getUsuarioPorId(id)

        val idRol = rolNombreToId(usuarioActual.rol)
        val idEstado = estadoNombreToId(usuarioActual.estado)

        // 2) Convertimos bytes -> Base64 (o null si quieres borrar la foto)
        val fotoBase64 = when {
            bytesImagen == null || bytesImagen.isEmpty() -> null
            else -> Base64.encodeToString(bytesImagen, Base64.NO_WRAP)
        }

        // 3) Armamos el body para el PUT
        val body = UsuarioRequestDto(
            nombre = usuarioActual.nombre,
            correo = usuarioActual.correo,
            // ⚠ Aquí es clave lo del backend: que NO fuerce actualizar la clave si viene vacía
            clave = "",
            idRol = idRol,
            idEstado = idEstado,
            puntaje = usuarioActual.puntaje,
            puntajeGlobal = usuarioActual.puntajeGlobal,
            fotoPerfilBase64 = fotoBase64
        )

        // 4) Hacemos el PUT
        return api.actualizarUsuario(id, body)
    }

    // data/repository/AuthRepository.kt
    suspend fun login(
        identificador: String,
        clave: String
    ): UsuarioResponseDto? {
        return try {
            val body = LoginRequestDto(
                identificador = identificador,
                clave = clave
            )
            api.login(body)
        } catch (e: retrofit2.HttpException) {
            // Si el backend devuelve 401 => credenciales inválidas
            if (e.code() == 401) {
                null
            } else {
                throw e
            }
        }
    }

    suspend fun getRecoveryQuestions(identificador: String) = runCatching {
        api.getRecoveryQuestions(identificador)
    }

    suspend fun verifyRecovery(userId: Long, items: List<VerifyRecoveryRequestDto.Item>) = runCatching {
        api.verifyRecovery(VerifyRecoveryRequestDto(userId, items))
    }

    suspend fun resetPasswordRecovery(token: String, newPassword: String) = runCatching {
        val resp = api.resetPasswordRecovery(ResetPasswordRequestDto(token, newPassword))
        if (!resp.isSuccessful) error("HTTP ${resp.code()}")
    }

    suspend fun getSecurityQuestions(): Result<List<SecurityQuestionDto>> = runCatching {
        api.getSecurityQuestions()
    }

    suspend fun setupSecurityQuestions(
        userId: Long,
        items: List<SetupSecurityQuestionsRequestDto.Item>
    ): Result<Unit> = runCatching {
        api.setupSecurityQuestions(
            SetupSecurityQuestionsRequestDto(userId = userId, items = items)
        )
        Unit
    }


}