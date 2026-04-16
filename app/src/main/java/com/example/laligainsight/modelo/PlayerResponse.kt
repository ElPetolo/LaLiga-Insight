package com.example.laligainsight.modelo

// Este modelo representa la respuesta completa de la API de football-data
// No sustituye a Player, lo envuelve
data class PlayerResponse(
    val squad: List<Player>
)