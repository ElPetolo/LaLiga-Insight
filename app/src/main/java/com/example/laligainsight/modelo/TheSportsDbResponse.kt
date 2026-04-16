package com.example.laligainsight.modelo

// Respuesta principal de búsqueda de jugador por nombre
data class TheSportsDbResponse(
    val player: List<TheSportsDbPlayer>?
)

// Información básica del jugador devuelta por TheSportsDB
data class TheSportsDbPlayer(
    val idPlayer: String?,
    val strPlayer: String?,
    val strTeam: String?,
    val strThumb: String?,      // Foto normal
    val strCutout: String?,     // Imagen recortada
    val strNationality: String?,
    val dateBorn: String?,
    val strPosition: String?
)