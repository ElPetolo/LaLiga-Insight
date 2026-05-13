package com.example.laligainsight.modelo

// MODELOS DE DATOS PARA OBBTENER LA CLASIFICACIÓN DE LALIGA DESDE LA API

/* Respuesta principal de la clasificación de la liga española */
data class RespuestaClasificacion(
    val standings: List<Standing>
)

// Representa un bloque de clasificación
// Hay tres tipos de clasificación: TOTAL, HOME, AWAY
// Nos interesa principalmente TOTAL
data class Standing(
    val type: String,
    val table: List<StandingTeam>
)


// Representa cada equipo dentro de la tabla de clasificación
data class StandingTeam(
    val position: Int,
    val team: StandingTeamInfo,
    val playedGames: Int,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int
)


// Información básica del equipo --> id, nombre y escudo
data class StandingTeamInfo(
    val id: Int,
    val name: String,
    val crest: String
)