package com.example.laligainsight.modelo

// Modelo de datos para almacenar información de los jugadores en Firebase
data class FirebasePlayer(
    val imageUrl: String = "",
    val playerName: String = "",
    val teamName: String = ""
)