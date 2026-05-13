package com.example.laligainsight.modelo

// Modelo de datos para la respuesta de la API de equipos de la liga española
data class RespuestaEquipos (
    val teams: List<Equipo>
)