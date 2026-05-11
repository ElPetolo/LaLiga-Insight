package com.example.laligainsight.modelo

// Modelo de datos para obtener los detalles de un equipo
data class TeamDetailResponse(
    val squad: List<Player>
)


// Un equipo (squad) esta formado por numerosos jugadores
// Cada jugador tiene sus propios datos
data class Player(
    val id: Int,
    val name: String,
    val position: String?,
    val dateOfBirth: String?,
    val nationality: String?,
    val shirtNumber: Int?
)