package com.example.laligainsight.autenticacion

data class EstadoAutenticacion(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)