package com.example.quizapp.domain.validation

import android.util.Patterns // Usamos el patrón estándar de Android para emails

// Valida que el email no esté vacío y cumpla patrón de email
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio"

    // Intentamos usar el patrón de Android si existe
    val androidPattern = Patterns.EMAIL_ADDRESS
    val ok = androidPattern?.matcher(email)?.matches()
    // Si es null (como en tests unitarios), usamos un regex simple de respaldo
        ?: Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)

    return if (!ok) "Formato de email inválido" else null
}

// Nombre de Usuario
fun validateNameLettersOnly(name: String): String? {                   // Valida nombre
    if (name.isBlank()) return "El nombre es obligatorio"              // Regla 1: no vacío
    if (name.length < 6) return "Mínimo 6 caracteres"                  // Largo mínimo

    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ0-9]+$")                      // Regla 2: solo letras y numeros
    return if (!regex.matches(name)) "Solo letras y numeros" else null// Mensaje si falla


}


// Valida seguridad de la contraseña (mín. 8, mayús, minús, número y símbolo; sin espacios)
fun validateStrongPassword(pass: String): String? {                    // Requisitos mínimos de seguridad
    if (pass.isBlank()) return "La contraseña es obligatoria"          // No vacío
    if (pass.length < 8) return "Mínimo 8 caracteres"                  // Largo mínimo
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula" // Al menos 1 mayúscula
    if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula" // Al menos 1 minúscula
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"         // Al menos 1 número
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo" // Al menos 1 símbolo
    if (pass.contains(' ')) return "No debe contener espacios"          // Sin espacios
    return null                                                         // OK
}

// Valida que la confirmación coincida con la contraseña
fun validateConfirm(pass: String, confirm: String): String? {          // Confirmación de contraseña
    if (confirm.isBlank()) return "Confirma tu contraseña"             // No vacío
    return if (pass != confirm) "Las contraseñas no coinciden" else null // Deben ser iguales

}
