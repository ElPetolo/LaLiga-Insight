package com.example.laligainsight.autenticacion

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RepositorioAutenticacion {

    private val auth = FirebaseAuth.getInstance()

    // Devuelve el usuario autenticado si la sesión sigue activa.
    fun currentUser() = auth.currentUser

    // Inicio de sesión clásico con correo y contraseña.
    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    // Crea la cuenta en Firebase Authentication.
    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    // Envía el correo para restablecer la contraseña.
    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // Cierra la sesión local del usuario.
    fun logout() {
        auth.signOut()
    }
}
