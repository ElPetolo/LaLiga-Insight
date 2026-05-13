package com.example.laligainsight.autenticacion

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RepositorioAutenticacion {

    private val auth = FirebaseAuth.getInstance()

    fun currentUser() = auth.currentUser

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        auth.signOut()
    }
}