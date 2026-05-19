package com.example.laligainsight.modelo

// Resumen listo para la UI con media global, número de votos y nota del usuario actual.
data class ResumenValoracion(
    val average: Double = 0.0,
    val count: Int = 0,
    val userRating: Int = 0
)
