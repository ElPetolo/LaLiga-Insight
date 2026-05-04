package com.example.laligainsight.modelo

// Respuesta de la API de goleadores


// Lista principal de goleadores que devuelve la API
data class ScorersResponse(
    val scorers: List<Scorer>
)

// Información de cada goleador
data class Scorer(
    val player: PlayerScorer,
    val team: TeamScorer,
    val playedMatches: Int,
    val goals: Int,
    val assists: Int,
) {

    // Suma de goles + asistencias
    val scorerPoints: Int
        get() = goals + (assists ?: 0)

    // Cálculo de goles por partido
    val goalsPerMatch: Float
        get() = if (playedMatches > 0) goals.toFloat() / playedMatches else 0f

    // Cálculo de la suma de goles/asistencias (val scorerPoints) por partido
    val scorerPointsPerMatch: Float
        get() = if (playedMatches > 0) scorerPoints.toFloat() / playedMatches else 0f
}


// Datos básicos del goleador --> id, nombre, nacionalidad
data class PlayerScorer(
    val id: Int,
    val name: String,
    val nationality: String?
)

// Datos del equipo en el que juega el goleador
data class TeamScorer(
    val id: Int,
    val name: String,
    val crest: String?
)