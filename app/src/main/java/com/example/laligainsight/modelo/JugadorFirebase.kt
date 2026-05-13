package com.example.laligainsight.modelo

// Modelo de datos para almacenar información de los jugadores en Firebase
data class JugadorFirebase(
    val imageUrl: String = "",
    val playerName: String = "",
    val teamName: String = ""
)