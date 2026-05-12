package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.laligainsight.modelo.Match

fun calcularResultadoPartido(match: Match, teamId: Int): String{

    // Variables para goles del equipo local y visitante
    val homeGoals = match.score.fullTime.home ?:0
    val awayGoals = match.score.fullTime.away ?:0

    // Comprobamos si el equipo es loca
    val isHomeTeam = match.homeTeam.id == teamId

    return when {
        homeGoals == awayGoals -> "E"
        isHomeTeam && homeGoals > awayGoals -> "G"
        isHomeTeam && homeGoals < awayGoals -> "P"
        !isHomeTeam && awayGoals > homeGoals -> "G"
        else -> "P"
    }
}


// Funcion para traducir el nombre de la posicion en el campo a español.
fun traducirPosicion(position: String?): String {
    return when (position) {
        "Goalkeeper" -> "Portero"
        "Centre-Back" -> "Defensa central"
        "Left-Back" -> "Lateral izquierdo"
        "Right-Back" -> "Lateral derecho"
        "Defence" -> "Defensa"

        "Defensive Midfield" -> "Mediocentro defensivo"
        "Central Midfield" -> "Centrocampista"
        "Attacking Midfield" -> "Mediocentro ofensivo"
        "Midfield" -> "Centrocampista"
        "Left Midfield" -> "Interior izquierdo"
        "Right Midfield" -> "Interior derecho"

        "Left Winger" -> "Extremo izquierdo"
        "Right Winger" -> "Extremo derecho"
        "Offence" -> "Atacante"
        "Second Striker" -> "Segundo delantero"
        "Centre-Forward" -> "Delantero centro"
        "Attacker" -> "Delantero"


        else -> position ?: "Posición no disponible"
    }
}
// Funcion para pasar la hora UTC de la API a la hora española
@RequiresApi(Build.VERSION_CODES.O)
fun formatoHoraPartido(utcDate: String): String {
    return try {
        // Convertimos la hora UTC a un objeto Date
        val instant = java.time.Instant.parse(utcDate)

        // Formateamos la hora a la zona horaria de España
        val horaEspanyola = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.of("Europe/Madrid"))

        // Devolvemos la hora formateada en HH:mm
        horaEspanyola.toLocalTime().toString().substring(0,5)

    }   catch (e: Exception) {
        // Mostramos placeHolder
        "--:--"
    }
}

// Funcion para mostrar la fecha de partido en formato dd/MM
@RequiresApi(Build.VERSION_CODES.O)
fun formatoFechaPartido(utcDate: String): String {

    return try {
        val instant = java.time.Instant.parse(utcDate)

        // Formateamos la fecha a la zona horaria de España
        val fechaEspanyola = java.time.LocalDateTime.ofInstant(
            instant,
            java.time.ZoneId.of("Europe/Madrid")
        )

        val dia = fechaEspanyola.dayOfMonth.toString().padStart(2, '0')
        val mes = fechaEspanyola.monthValue.toString().padStart(2, '0')

        // Devolvemos la fecha formateada en dd/MM
        "$dia/$mes"

    } catch (e: Exception) {
        // Mostramos placeHolder
        "--/--"
    }
}