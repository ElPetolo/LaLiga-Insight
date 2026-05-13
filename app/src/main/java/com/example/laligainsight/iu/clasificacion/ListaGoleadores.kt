package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.JugadorFirebase
import com.example.laligainsight.modelo.Scorer

@Composable
fun ListaGoleadores(
    scorers: List<Scorer>,
    players: List<JugadorFirebase>,
    isLoading: Boolean,
    error: String?
) {
    when {
        isLoading -> {
            CircularProgressIndicator(color = Color.White)
        }

        error != null -> {
            Text(
                text = error,
                color = Color.Red
            )
        }

        else -> {
            Column {
                // La cabecera se mantiene fuera de la lista para que siempre quede arriba.
                ScorersHeader()
                LazyColumn {
                    items(scorers) { scorer ->
                        ScorerItem(
                            scorer = scorer,
                            players = players
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScorerItem(scorer: Scorer, players: List<JugadorFirebase>) {
    // Aquí buscamos la imagen del jugador usando los datos guardados en Firebase.
    val imageUrl = players
        .firstOrNull { it.playerName == scorer.player.name }
        ?.imageUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                color = ColoresApp.CardDark,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bloque principal con foto, nombre y equipo del goleador.
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = scorer.player.name,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = scorer.player.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Text(
                    text = scorer.team.name,
                    color = Color(0xFFBFC7D5),
                    fontSize = 10.sp
                )
            }
        }

        // Estas columnas muestran exactamente las mismas estadísticas de la versión original.
        Text("${scorer.scorerPoints}", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("${scorer.goals}", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("${scorer.assists ?: 0}", color = Color.White, modifier = Modifier.weight(0.6f))

        Text(
            text = "%.2f".format(scorer.scorerPointsPerMatch),
            color = Color.White,
            modifier = Modifier.weight(0.8f),
            fontSize = 12.sp
        )

        Text(
            text = "%.2f".format(scorer.goalsPerMatch),
            color = Color.White,
            modifier = Modifier.weight(0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun ScorersHeader() {
    // Cabecera fija para que las abreviaturas de las columnas se entiendan mejor.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("JUGADOR", color = Color.White, modifier = Modifier.weight(2f))
        Text("G/A", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("G", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("A", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("⌀ G/A", color = Color.White, modifier = Modifier.weight(0.8f))
        Text("⌀ G", color = Color.White, modifier = Modifier.weight(0.8f))
    }
}
