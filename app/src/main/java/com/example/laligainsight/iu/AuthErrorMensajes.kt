package com.example.laligainsight.iu

import com.google.firebase.auth.FirebaseAuthException

fun getFriendlyAuthError(e: Exception): String {
    val code = (e as? FirebaseAuthException)?.errorCode

    return when (code) {
        "ERROR_INVALID_EMAIL" -> "El correo electrónico no tiene un formato válido"
        "ERROR_USER_NOT_FOUND" -> "No existe ninguna cuenta con este correo"
        "ERROR_WRONG_PASSWORD" -> "La contraseña no es correcta"
        "ERROR_INVALID_CREDENTIAL" -> "El correo o la contraseña no son correctos"
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con este correo"
        "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
        "ERROR_NETWORK_REQUEST_FAILED" -> "Revisa tu conexión a internet"
        "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Inténtalo más tarde"
        else -> "Ha ocurrido un error. Inténtalo de nuevo"
    }
}