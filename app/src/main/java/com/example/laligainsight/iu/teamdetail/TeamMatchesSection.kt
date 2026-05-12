package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.Match

@Composable
fun BotonPartidos(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Botón reutilizable para cambiar entre "Resultados" y "Próximos".
    // Cambia de color dependiendo de si está seleccionado o no.
    Card(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AppColors.AccentGreen else AppColors.ButtonSecondary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PartidoItem(
    match: Match,
    teamId: Int,
    isFinished: Boolean,
    showBadge: Boolean = true
) {
    // Fila que representa un partido.
    // Sirve tanto para partidos finalizados como para partidos pendientes.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hora del partido formateada a hora española.
        Text(
            text = formatoHoraPartido(match.utcDate),
            color = Color(0XFFB0B0B0),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(55.dp)
        )

        // Zona central con equipo local y visitante.
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EquipoLinea(match.homeTeam.crest, match.homeTeam.name)
            EquipoLinea(match.awayTeam.crest, match.awayTeam.name)
        }

        if (isFinished) {
            // Si el partido está finalizado, mostramos el marcador.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = match.score.fullTime.home?.toString() ?: "-",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = match.score.fullTime.away?.toString() ?: "-",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge opcional con G, E o P.
                // En algunas pantallas lo ocultamos, por eso está el parámetro showBadge.
                if (showBadge) {
                    ResultadoBadge(match = match, teamId = teamId)
                }
            }
        } else {
            // Si el partido todavía no se ha jugado, mostramos la fecha.
            Text(
                text = formatoFechaPartido(match.utcDate),
                color = Color(0XFFB0B0B0),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResultadoBadge(
    match: Match,
    teamId: Int
) {
    // Calculamos los goles del partido.
    val homeGoals = match.score.fullTime.home ?: 0
    val awayGoals = match.score.fullTime.away ?: 0

    // Comprobamos si el equipo que estamos viendo era local.
    val isHomeTeam = match.homeTeam.id == teamId

    // Según el marcador, calculamos si el equipo ganó, empató o perdió.
    val resultado = when {
        homeGoals == awayGoals -> "E"
        isHomeTeam && homeGoals > awayGoals -> "G"
        isHomeTeam && homeGoals < awayGoals -> "P"
        !isHomeTeam && homeGoals < awayGoals -> "G"
        !isHomeTeam && homeGoals > awayGoals -> "P"
        else -> "P"
    }

    // Color del badge según el resultado.
    val badgeColor = when (resultado) {
        "G" -> Color(0xFF4CAF50)
        "P" -> Color(0xFFEF5350)
        "E" -> Color(0xFFFFC107)
        else -> Color.Gray
    }

    // Caja pequeña donde se muestra G, E o P.
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = resultado,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EquipoLinea(
    crest: String?,
    name: String
) {
    // Línea de un equipo dentro de un partido.
    // Muestra escudo + nombre.
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = crest,
            contentDescription = name,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamMatchesSection(
    matches: List<Match>,
    teamId: Int
) {
    // Sección completa de partidos del equipo.
    // Permite alternar entre resultados ya jugados y próximos partidos.
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Estado interno para saber qué pestaña de partidos está activa.
        var selectedMatchTab by remember { mutableStateOf("Resultados") }

        // Partidos finalizados ordenados para ver primero los más recientes.
        val resultados = matches
            .filter { it.status == "FINISHED" }
            .reversed()

        // Partidos pendientes o con hora asignada.
        val proximos = matches.filter {
            it.status == "SCHEDULED" || it.status == "TIMED"
        }

        // Botones superiores para cambiar entre resultados y próximos.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonPartidos(
                text = "RESULTADOS",
                selected = selectedMatchTab == "Resultados",
                modifier = Modifier.weight(1f)
            ) {
                selectedMatchTab = "Resultados"
            }

            BotonPartidos(
                text = "PRÓXIMOS",
                selected = selectedMatchTab == "Próximos",
                modifier = Modifier.weight(1f)
            ) {
                selectedMatchTab = "Próximos"
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Según la opción seleccionada, decidimos qué lista se muestra.
        val listToShow = if (selectedMatchTab == "Resultados") resultados else proximos

        // Pintamos todos los partidos de la lista seleccionada.
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listToShow.forEach { match ->
                PartidoItem(
                    match = match,
                    teamId = teamId,
                    isFinished = match.status == "FINISHED"
                )
            }
        }
    }
}