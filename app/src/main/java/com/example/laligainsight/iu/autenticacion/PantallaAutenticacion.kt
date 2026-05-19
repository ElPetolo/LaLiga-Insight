package com.example.laligainsight.iu

import androidx.compose.runtime.*

@Composable
// Esta pantalla solo decide si mostramos login o registro según el estado local.
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
