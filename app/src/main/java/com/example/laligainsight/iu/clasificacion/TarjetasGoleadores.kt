package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.JugadorFirebase
import com.example.laligainsight.modelo.Scorer

@Composable
// Vista alternativa de goleadores en formato cards en lugar de tabla.
fun TarjetasGoleadores(
    scorers: List<Scorer>,
    players: List<JugadorFirebase>,
    isLoading: Boolean,
    error: String?
) {
    when {
        isLoading -> {
            // Mientras llegan los datos mostramos solo el indicador de carga.
            CircularProgressIndicator(color = Color.White)
        }

        error != null -> {
            // Si falla la carga, enseñamos el mensaje recibido.
            Text(text = error, color = Color.Red)
        }

        else -> {
            // Cuando todo está listo, pintamos una card por goleador.
            LazyColumn {
                itemsIndexed(scorers) { index, scorer ->
                    ScorersCardItem(
                        scorer = scorer,
                        position = index + 1,
                        players = players
                    )
                }
            }
        }
    }
}

@Composable
// Card individual de un goleador con posición, foto, escudo y goles.
fun ScorersCardItem(scorer: Scorer, position: Int, players: List<JugadorFirebase>) {
    // El degradado cambia según la posición para destacar el top 3.
    val cardBrush = when (position) {
        1 -> Brush.horizontalGradient(listOf(Color(0xFFE4C313), Color(0xFFD9C878)))
        2 -> Brush.horizontalGradient(listOf(Color(0xFFC9CED6), Color(0xFFE3E7EF)))
        3 -> Brush.horizontalGradient(listOf(Color(0xFFC06A00), Color(0xFFD4A06A)))
        else -> Brush.horizontalGradient(listOf(ColoresApp.CardDark, ColoresApp.CardSoftStrong))
    }

    // La foto del jugador sale de la misma lista de jugadores de Firebase.
    val imageUrl = players
        .firstOrNull { it.playerName == scorer.player.name }
        ?.imageUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .background(
                brush = cardBrush,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del jugador obtenida desde Firebase.
        AsyncImage(
            model = imageUrl,
            contentDescription = scorer.player.name,
            modifier = Modifier.size(46.dp)
        )

        // Número de la posición dentro del ranking.
        Text(
            text = "$position.",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp)
        )

        // Escudo del equipo actual del goleador.
        AsyncImage(
            model = scorer.team.crest,
            contentDescription = scorer.team.name,
            modifier = Modifier.size(36.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            // Nombre del jugador y dato principal de goles.
            Text(
                text = scorer.player.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${scorer.goals} GOLES",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
// Selector simple para cambiar entre la vista de cards y la tabla de goleadores.
fun ScorersViewSelector(selectedMode: String, onModeSelected: (String) -> Unit) {
    // Este selector solo cambia entre vista en cards y vista en tabla.
    val modes = listOf("CARDS", "TABLA")

    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        modes.forEach { mode ->
            val selected = selectedMode == mode

            Text(
                text = mode,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (selected) ColoresApp.AccentGreen else ColoresApp.CardDark,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onModeSelected(mode) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
