package com.example.laligainsight.iu

import androidx.compose.runtime.*

@Composable
fun PantallaAutenticacion(
    onLoginSuccess: () -> Unit
) {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        PantallaRegistro(
            onRegisterSuccess = onLoginSuccess,
            onGoToLogin = { showRegister = false }
        )
    } else {
        PantallaLogin(
            onLoginSuccess = onLoginSuccess,
            onGoToRegister = { showRegister = true }
        )
    }
}