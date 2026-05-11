package com.example.laligainsight.modelo

// Modelo de datos que recogemos de la respuesta de la API al obtener los partidos

// Data class para la respuesta de la API
data class MatchesResponse(
    val matches: List<Match>
)

// Data class para cada partido INDIVIDUAL
data class Match(
    val utcDate: String, // Fecha y hora del partido
    val status: String, // Estado del partido (FINISHED == finalizado, SCHEDUELD == próximo/por jugar...)
    val matchday: Int?, // Jornada
    val venue: String?, // Estadio donde se juega
    val homeTeam: MatchTeam,
    val awayTeam: MatchTeam,
    val score: Score // Resultado del partido
)

// Data class que representa un equipo dentro del partido
data class MatchTeam(
    val id: Int,
    val name: String,
    val crest: String?
)


// Data class que representa el marcador del partido
data class Score(
    val fullTime: FullTimeSocre
)

// Data class que representa el resultado final (goles de cada conjunto)
// Pueden ser valores nulos si aún no se ha jugado el partido
data class FullTimeSocre(
    val home: Int?, // Goles del equipo local
    val away: Int? // Goles del equipo visitante
)