package com.example.laligainsight.modelo

// Clase para presentar información extra de un jugador dada por la API
data class InformacionExtraJugador(
    val fullName: String,
    val position: String,
    val birthday: String,
    val nationality: String,
    val currentTeam: String,
    val contract: String,
    val marketValue: String,
    val preferredFoot: String,
    val matchesInDatabase: String,
    val relatedItems: List<String>
)