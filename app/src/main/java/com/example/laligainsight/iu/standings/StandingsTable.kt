package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.StandingTeam

@Composable
fun StandingsTable(standings: List<StandingTeam>) {
    // Esta tabla agrupa cabecera y filas para que la pantalla principal quede más limpia.
    StandingHeader()
    LazyColumn {
        items(standings) { team ->
            StandingItem(team = team)
        }
    }
}

@Composable
fun StandingItem(team: StandingTeam) {
    // El color lateral depende de la zona de clasificación en la tabla.
    val accentColor = when {
        team.position in 1..5 -> Color(0xFF1D75D8)
        team.position == 6 || team.team.name.contains("Real Sociedad", true) -> Color(0xFFFF6A00)
        team.position == 7 -> Color(0xFF2ECC71)
        team.position >= 18 -> Color(0xFFE53935)
        else -> Color(0xFF30415F)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        AppColors.CardDark,
                        accentColor.copy(alpha = 0.75f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Este bloque muestra la posición como si fuera una etiqueta lateral.
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(30.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${team.position}.",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        AsyncImage(
            model = team.team.crest,
            contentDescription = team.team.name,
            modifier = Modifier.width(28.dp).height(28.dp)
        )

        Text(
            text = team.team.name,
            color = Color.White,
            modifier = Modifier
                .weight(1.6f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold
        )

        Text("${team.playedGames}", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("${team.points}", color = Color.White, modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold)
        Text("${team.goalDifference}", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("${team.goalsFor}:${team.goalsAgainst}", color = Color.White, modifier = Modifier.weight(0.9f))
    }
}

@Composable
fun StandingHeader() {
    // Cabecera simple para que las columnas de la clasificación queden claras.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("POS", color = Color.White, modifier = Modifier.width(28.dp))
        Spacer(modifier = Modifier.width(28.dp))
        Text("TEAM", color = Color.White, modifier = Modifier.weight(1.6f))
        Text("PJ", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("PTS", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("+/-", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("GOLES", color = Color.White, modifier = Modifier.weight(0.9f))
    }
}
