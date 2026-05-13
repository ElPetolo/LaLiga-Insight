package com.example.laligainsight.iu

import androidx.compose.runtime.*

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        RegisterScreen(
            onRegisterSuccess = onLoginSuccess,
            onGoToLogin = { showRegister = false }
        )
    } else {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onGoToRegister = { showRegister = true }
        )
    }
}