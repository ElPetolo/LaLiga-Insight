package com.example.laligainsight.autenticacion

// Estado simple para pintar la UI de autenticación según el progreso y el resultado final.
data class EstadoAutenticacion(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
