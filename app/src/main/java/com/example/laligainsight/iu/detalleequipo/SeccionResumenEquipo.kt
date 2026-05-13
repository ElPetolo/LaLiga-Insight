package com.example.laligainsight.iu

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.modelo.Match
import com.example.laligainsight.modelo.StandingTeam

@Composable
fun MiniStatCard(
    value: String,
    label: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    // Card pequeña para mostrar un dato de clasificación.
    // Por ejemplo: posición, puntos, victorias, empates, derrotas...
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Línea decorativa superior. Usa el mismo color que el valor principal.
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .background(valueColor, shape = RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Valor principal de la estadística.
            Text(
                text = value,
                color = valueColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Texto descriptivo de la estadística.
            Text(
                text = label,
                color = Color(0xFF5A6A8A),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun GoalStatCard(
    value: String,
    label: String,
    icon: String,
    mainColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    // Card usada para los goles a favor y goles en contra.
    // Lleva un icono, un valor grande y una etiqueta.
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Caja del icono, con fondo propio para destacar la estadística.
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = mainColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                // Número principal, por ejemplo goles a favor o en contra.
                Text(
                    text = value,
                    color = mainColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // Descripción del dato.
                Text(
                    text = label,
                    color = Color(0xFF8D99B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CirculoRacha(resultado: String) {
    // Círculo pequeño que muestra el resultado de un partido:
    // G = ganado, E = empatado, P = perdido.
    val color = when (resultado) {
        "G" -> Color(0xFF4CAF50)
        "E" -> Color(0xFFFFC107)
        "P" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = resultado,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    // Título visual para separar bloques dentro del resumen.
    // Se usa en clasificación, últimos partidos, goles y progreso de temporada.
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF4A90E2), shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text.uppercase(),
            color = Color(0xFF8899BB),
            fontSize = 15.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SeccionResumenEquipo(
    teamStanding: StandingTeam?,
    matches: List<Match>,
    teamId: Int
) {
    // Sección principal de resumen del equipo.
    // Aquí se juntan datos de clasificación, racha, goles y progreso de temporada.
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (teamStanding != null) {

            // Diferencia de goles con formato + o - para que se vea más claro.
            val difGoles = teamStanding.goalDifference
            val diffStr = if (difGoles >= 0) "+$difGoles" else "$difGoles"

            // Bloque de clasificación general del equipo.
            SectionLabel("CLASIFICACIÓN")

            // Grid de 2 columnas para enseñar estadísticas principales.
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(295.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                item { MiniStatCard("${teamStanding.position}º", "Posición") }
                item { MiniStatCard("${teamStanding.points}", "Puntos") }
                item { MiniStatCard("${teamStanding.won}", "Victorias") }
                item { MiniStatCard("${teamStanding.draw}", "Empates") }
                item { MiniStatCard("${teamStanding.lost}", "Derrotas") }
                item { MiniStatCard(diffStr, "Dif. goles") }
            }

            // Cogemos los últimos 5 partidos finalizados para mostrar la racha.
            val ultimos5Partidos = matches
                .filter { it.status == "FINISHED" }
                .sortedByDescending { it.utcDate }
                .take(5)

            SectionLabel("ÚLTIMOS 5 PARTIDOS")

            // Pintamos la racha con círculos G, E o P.
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ultimos5Partidos.forEach { match ->
                    val resultado = calcularResultadoPartido(match, teamId)
                    CirculoRacha(resultado)
                }
            }

            // Bloque de goles a favor y en contra.
            SectionLabel("GOLES")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalStatCard(
                    value = teamStanding.goalsFor.toString(),
                    label = "A favor",
                    icon = "↑",
                    mainColor = Color(0xFF4CAF50),
                    backgroundColor = Color(0xFF143D2A),
                    modifier = Modifier.weight(1f)
                )

                GoalStatCard(
                    value = teamStanding.goalsAgainst.toString(),
                    label = "En contra",
                    icon = "↓",
                    mainColor = Color(0xFFEF5350),
                    backgroundColor = Color(0xFF4A1E1E),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progreso de temporada según partidos jugados de 38 jornadas.
            SectionLabel("PROGRESO DE TEMPORADA")

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${teamStanding.playedGames} / 38 jornadas",
                    color = Color(0xFF8899BB),
                    fontSize = 17.sp
                )

                Text(
                    text = "${((teamStanding.playedGames / 38f) * 100).toInt()}%",
                    color = Color(0xFF4A90E2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra que representa visualmente el avance de la temporada.
            LinearProgressIndicator(
                progress = { teamStanding.playedGames / 38f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF4A90E2),
                trackColor = Color(0xFF1C2B4A)
            )
        } else {
            // Texto de carga mientras todavía no llegaron los datos de clasificación.
            Text(
                text = "Cargando datos...",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}